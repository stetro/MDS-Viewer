var FileViewerController = function ($scope, $http, $injector) {
    $scope.loadFiles = function () {
        $scope.showFileSpinner = true;
        $http({
            method: 'GET',
            url: '/api/tree/?root=/'
        }).success(function (data, status, headers, config) {
                $scope.files = data;
                $scope.showFileSpinner = false;
                window.setTimeout(function () {
                    $(".filetree").treeview({collapsed: true});
                }, 500);
            }).error(function (data, status, headers, config) {
                $scope.alert = "Cannot load filetree !"
                $scope.files = {};
                $scope.showFileSpinner = false;
            });
    }
    $scope.loadFiles();

    $scope.selectFile = function (file) {
        if (file.data.type == "file") {
            $scope.currentFile = file;
            $scope.currentFolder = false;
            loadCodeOf(file.data.fullpath);
        }
        else {
            $scope.currentFolder = file;
            $scope.selectedFiles = file.children;
            $scope.code = null;
            $scope.newFileName = null;
            $scope.newFolderName = null;
            $scope.currentFile = false;
            loadDirectoryDetails(file);
        }
    }

    $scope.deleteFile = function (file) {
        $http({
            method: 'DELETE',
            url: '/api/file/?path=' + file.data.fullpath
        }).success(function (data, status, headers, config) {
                $scope.currentFile = null;
                refreshCode();
                deleteFileFromTree(file, $scope.files);
            }).error(function (data, status, headers, config) {
                $scope.alert = "Cannot delete file !"
            });
    }
    $scope.deleteFolder = function (currentFolder) {
        $http({
            method: 'DELETE',
            url: '/api/folder/?path=' + currentFolder.data.fullpath
        }).success(function (data, status, headers, config) {
                $scope.currentFolder = $scope.files;
                deleteFileFromTree(currentFolder, $scope.files);
            }).error(function (data, status, headers, config) {
                $scope.alert = "Cannot delete folder !"
            });
    }
    $scope.hideAlert = function () {
        $scope.alert = false;
    }
    $scope.saveFile = function (file) {
        var data = $.param({data: $scope.code, path: $scope.currentFile.data.fullpath});
        $http({
            method: 'POST',
            url: '/api/file/',
            data: data,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).success(function (data, status, headers, config) {
                if (data.error) {
                    $scope.alert = "Could not update file !"
                }
                else {
                    loadCodeOf($scope.currentFile.data.fullpath);
                }
            }).error(function (data, status, headers, config) {
                $scope.alert = "Cannot edit file !"
            });
    }

    $scope.loadVersions = function () {
        $scope.showCodeSpinner = true;
        $http({
            method: 'GET',
            url: '/api/file/versions/?path=' + $scope.currentFile.data.fullpath
        }).success(function (data, status, headers, config) {
                $scope.currentFileVersions = data;
                $scope.showCodeSpinner = false;
            }).error(function (data, status, headers, config) {
                $scope.alert = "No versions available !"
                $scope.showCodeSpinner = false;
            });
    }

    $scope.loadCodeOfVersion = function () {
        var version = $scope.currentFileSelectedVersion;
        $scope.showCodeSpinner = true;
        $http({
            method: 'GET',
            url: '/api/file/' + version + '?path=' + $scope.currentFile.data.fullpath
        }).success(function (data, status, headers, config) {
                $scope.code = data;
                $scope.currentFileVersions = null;
                refreshCode(data);
                $scope.downloadLink = 'api/file/download/' + version + '?path=' + $scope.currentFile.data.fullpath;
                $scope.loadedVersion = version;
                $scope.showCodeSpinner = false;
            }).error(function (data, status, headers, config) {
                $scope.alert = "Could not load version of file !"
                $scope.showCodeSpinner = false;
            });
    }

    $scope.addFile = function (filename) {
        if (filename !== undefined && filename.length > 0) {
            var filePath = $scope.currentFolder.data.fullpath + "/" + filename;
            var data = $.param({data: $scope.code, path: filePath});
            $http({
                method: 'POST',
                url: '/api/file/new/',
                data: data,
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data, status, headers, config) {
                    if (data.error) {
                        $scope.alert = "Could not create file !"
                    }
                    else {
                        var newFile = { "children": [],
                            "data": {"fullpath": filePath,
                                "path": filename,
                                "type": "file"
                            }};
                        addFileToTree($scope.currentFolder, newFile, $scope.files);
                        $(".filetree").treeview();
                        $scope.selectFile($scope.currentFolder);
                    }
                }).error(function (data, status, headers, config) {
                    $scope.alert = "Cannot create file !"
                });
        } else {
            $scope.alert = "Please enter a file name !";
        }
    }
    $scope.addFolder = function (filename) {
        if (filename !== undefined && filename.length > 0) {
            var filePath = $scope.currentFolder.data.fullpath + "/" + filename;
            var data = $.param({ path: filePath});
            $http({
                method: 'POST',
                url: '/api/folder/new/',
                data: data,
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (data, status, headers, config) {
                    if (data.error) {
                        $scope.alert = "Could not create file !"
                    }
                    else {
                        var newFile = { "children": [],
                            "data": {"fullpath": filePath,
                                "path": filename,
                                "type": "folder"
                            }, "children": []};
                        addFileToTree($scope.currentFolder, newFile, $scope.files);
                        $(".filetree").treeview();
                        $scope.selectFile($scope.currentFolder);
                    }
                }).error(function (data, status, headers, config) {
                    $scope.alert = "Cannot create folder !"
                });
        } else {
            $scope.alert = "Please enter a folder name !";
        }
    }

    $scope.executeDiff = function () {
        var firstVersion = $scope.firstSelectedVersion;
        var secondVersion = $scope.secondSelectedVersion;
        var path = $scope.currentFile.data.fullpath;
        $scope.showDiffSpinner = true;
        $http({
            method: 'GET',
            url: '/api/file/diff/?path=' + path + '&firstVersion=' + firstVersion + '&secondVersion=' + secondVersion
        }).success(function (data, status, headers, config) {
                $scope.showDiffSpinner = false;
                $scope.diffs = data;
            }).error(function (data, status, headers, config) {
                $scope.alert = "Cannot create diff !"
            });


    }

    var deleteFileFromTree = function (searchFile, root) {
        for (var i = 0; i < root.children.length; i++) {
            if (root.children[i] === searchFile) {
                root.children.remove(i);
                return;
            } else {
                deleteFileFromTree(searchFile, root.children[i]);
            }
        }
    }
    var addFileToTree = function (folder, newFile, root) {
        folder.children.push(newFile);
        /*
         for (var i = 0; i < root.children.length; i++) {
         if (root.children[i] === folder) {
         root.children[i].children.push(newFile);
         return;
         } else {
         addFileToTree(folder, newFile, root.children[i])
         }
         }    */
    }
    var loadDirectoryDetails = function (directory) {

    }
    var loadCodeOf = function (path) {
        $scope.loadedVersion = null;
        $scope.showCodeSpinner = true;
        $http({
            method: 'GET',
            url: '/api/file/?path=' + path
        }).success(function (data, status, headers, config) {
                $scope.code = data;
                $scope.currentFileVersions = null;
                refreshCode(data);
                $scope.downloadLink = 'api/file/download/?path=' + path;
                $scope.showCodeSpinner = false;
            }).error(function (data, status, headers, config) {
                $scope.alert = "Could not load content of file !"
                $scope.showCodeSpinner = false;
            });
    }

    var refreshCode = function (code) {
        $("#code.prettyprint").empty();
        $("#code.prettyprint").html("<code class=\"lang-xml\"></code>");
        $("code.lang-xml").text(code);
        prettyPrint();
    }
};

var mdsViewer = angular.module('mdsViewer', [])
    .controller("FileViewerController", FileViewerController).directive("downloadFile",function () {
        return function (scope, element, attrs) {
            scope.$watch('downloadLink', function () {
                element.context.href = document.location + scope.downloadLink;
            });
        };
    }).config(['$rootScopeProvider', function ($rootScopeProvider) {
    // maximum of filetreedepth should be infinity
    $rootScopeProvider.digestTtl(Infinity);
    }]).filter('diffcode',function(){
        return function(diff){
            if(diff.type=="DELETE"){
                return diff.original.lines.join("\n");
            }else{
                return diff.revised.lines.join("\n");
            }
        };
    });

// Array Remove - By John Resig (MIT Licensed)
Array.prototype.remove = function (from, to) {
    var rest = this.slice((to || from) + 1 || this.length);
    this.length = from < 0 ? this.length + from : from;
    return this.push.apply(this, rest);
};

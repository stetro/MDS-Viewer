'use strict';

describe('FileBrowser', function () {
    beforeEach(function () {
        browser().navigateTo('index.html');
    });

    it("should display preloader at the beginning",function(){
        expect(element("#fileSpinner").css("display")).equalsTo("block");
    });

    it("should list a tree of files by visiting the root page",function(){
        expect(element("#filetree").css("display")).toEqual("block");
        expect(element("#filetreedepth").css("display")).toEqual("block");
    });


});
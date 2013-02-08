files = [
    ANGULAR_SCENARIO,
    ANGULAR_SCENARIO_ADAPTER,
    '../../main/webapp/js/jquery-1.8.3.min.js',
    '../../main/webapp/js/angular.min.js',
    '../../main/webapp/js/bootstrap.min.js',
    'ui/*.spec.js'
];

autoWatch = true;

browsers = [ 'Chrome' ];

urlRoot = '/';

proxies = {
    '/': 'http://localhost:8080/'
};
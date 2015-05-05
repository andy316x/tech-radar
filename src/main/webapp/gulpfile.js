var gulp = require('gulp');
var inject = require('gulp-inject');
 
 
 
gulp.task('index', function () {
	return gulp.src('./raw/index.html')
	  .pipe(inject(gulp.src('./lib/controllers/*.js', {read: false}), {name: 'controllers', addPrefix: 'radar'}))
  	  .pipe(inject(gulp.src('./lib/directives/*.js', {read: false}), {name: 'directives', addPrefix: 'radar'}))
	  .pipe(gulp.dest('.'));
});
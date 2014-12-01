<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<script>
	 document.radar = {
	 	radarId: <%= request.getAttribute( "result" ) %>
	 };
	</script>

<!-- Bootstrap core JavaScript
    ================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="/radar/lib/jquery-1.11.1.min.js"></script>
<script src="/radar/lib/angular.min.js"></script>
<script src="/radar/lib/bootstrap.min.js"></script>
<script src="/radar/lib/d3.min.js" charset="utf-8"></script>
<script src="/radar/lib/radar.js"></script>
<script src="/radar/lib/controller.js"></script>
<script src="/radar/lib/app.js"></script>
<script src="/radar/lib/angular-route.min.js"></script>
<script src="/radar/lib/typeahead.js"></script>

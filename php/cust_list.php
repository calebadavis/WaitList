<!doctype html>
<html lang='en'>
  <head>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  </head>


  <body>
    <div id="mydata"/>
    <script>
setInterval(function(){ 
$.get( "mydata.php", function( data ) {
$( "#mydata" ).html( data ); 
});
 }, 5000); 
    </script>
  </body>
</html>

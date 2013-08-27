
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="../../assets/ico/favicon.png">
    <link href='http://fonts.googleapis.com/css?family=Roboto:400,500' rel='stylesheet' type='text/css'>
    <script type="text/javascript" src="/js/dygraph-combined.js"></script>
    <title>Hystocks</title>

    <!-- Bootstrap core CSS -->
    <link href="/css/bootstrap.css" rel="stylesheet">


    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="/js/html5shiv.js"></script>
      <script src="/js/respond.min.js"></script>
    <![endif]-->

  </head>

  <body style="font-family: 'Roboto', sans-serif;">



    <div class="container">

      <!-- Static navbar -->
      <div class="navbar navbar-default">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">Hystocks</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">

              <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown">Stocks <b class="caret"></b></a>
                  <ul class="dropdown-menu">
                      <li><a href="YHOO">Yahoo</a></li>
                      <li><a href="GOOG">Google</a></li>
                      <li><a href="FB">Facebook</a></li>
                      <li class="divider"></li>
                      <li class="dropdown-header">Soccer Teams</li>
                      <li><a href="MANU">Manchester United</a></li>
                  </ul>
              </li>

              <li class="dropdown">
                  <a href="#" class="dropdown-toggle" data-toggle="dropdown">JSON Data <b class="caret"></b></a>
                  <ul class="dropdown-menu">
                      <li><a href="data/YHOO">Yahoo</a></li>
                      <li><a href="data/GOOG">Google</a></li>
                      <li><a href="data/FB">Facebook</a></li>
                      <li class="divider"></li>
                      <li class="dropdown-header">Soccer Teams</li>
                      <li><a href="data/MANU">Manchester United</a></li>
                  </ul>
              </li>

          </ul>
        </div><!--/.nav-collapse -->
      </div>

      <!-- Main component for a primary marketing message or call to action -->
      <div class="jumbotron">

       <div class="row">
           <div class="col-md-8">
                 <div class="row"><div class="col-md-12"><span style="font-size:xx-large !important;">${name}</span></div></div>
                 <div class="row"><div class="col-md-12"><span style="font-size:large !important;">${e}:${t} - ${xlt}</span></div></div>
                 <div class="row"><div class="col-md-12"><span style="font-size:xx-large !important;">${l}</span><span style="font-size:x-large !important;"> ${c}</span><span style="font-size:x-large !important;"> (${cp})</span></div></div>
          </div>
          <div class="col-md-4">

              <table class="table" style="font-size:medium !important;">
                  <tr><td>Open</td><td>${op}</td></tr>
                  <tr><td>High</td><td>${hi}</td></tr>
                  <tr><td>Low</td><td>${lo}</td></tr>
                  <tr><td>Volume</td><td>${vo}</td></tr>
                  <tr><td>Avg Vol</td><td>${avvo}</td></tr>
                  <tr><td>Mkt Cap</td><td>${mc}</td></tr>
              </table>

          </div>

       </div>
       </div>

        <div id="graphdiv" style="width:100%; height:300px;"></div>
        <script type="text/javascript">
          g2 = new Dygraph(
            document.getElementById("graphdiv"),
            "/csv/${t}", // path to CSV file
             {
                 drawXGrid:false,
                 drawYGrid:false
                }         // options
          );
        </script>

    </div> <!-- /container -->

    <script src="/js/jquery-2.0.3.min.js"></script>
    <script src="/js/bootstrap.min.js"></script>


  </body>
</html>

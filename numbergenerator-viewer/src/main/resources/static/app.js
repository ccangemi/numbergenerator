var x = [];

var trace1 = {
  x: x,
  name: 'control',
  autobinx: false, 
  histnorm: "count", 
  marker: {
    color: "rgba(25, 109, 159, 0.7)",
     line: {
      color:  "rgba(48, 84, 109, 1)", 
      width: 1
    }
  },  
  opacity: 0.5, 
  type: "histogram", 
  xbins: {
    end: 100, 
    size: 1, 
    start: 1
  }
};
var data = [trace1];
var layout = {
  bargap: 0.1, 
  bargroupgap: 0.1, 
  //barmode: "overlay", 
  title: "Numbers distribution", 
  xaxis: {title: "Numbers", range: [1,100]}, 
  yaxis: {title: "Count"}
};


var stompClient = null;

function connect() {
    var socket = new SockJS('/numbers-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/numbers', function (number) {
            showNumbers(number.body);
        });
    });
}

function showNumbers(message) {
    data[0].x.push(message);
    Plotly.newPlot('myDiv', data, layout, {staticPlot: true, responsive: false});

}

updateNumbers();
data[0].x = numbers;
Plotly.newPlot('myDiv', data, layout, {staticPlot: true, responsive: false});
connect();

function localRefresh() {
  $('#anchor').load("/numbers", function() {
    updateNumbers();
    data[0].x = numbers;
    Plotly.react('myDiv', data, layout, {staticPlot: true, responsive: false});
}
)}
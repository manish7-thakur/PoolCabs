var location1;
var location2;
	
var address1;
var address2;

var lat1;
var lng1;
var geocoder;
var map;

var directionsService;
var directionsDisplay;
	
var distance;
        
function  initialize(){
    geocoder = new google.maps.Geocoder(); // creating a new geocode object
		
    // getting the two address values
    address1 = document.getElementById("pickupStreetAddress").value;
    address2 = document.getElementById("dropStreetAddress").value;
    //alert("" + address1+address2);
    if(!address1 || (0 === address1.length) || ! address2 || (0 === address2.length)){
        return true;
    }
		
    // finding out the coordinates
    if (geocoder)     {
        geocoder.geocode( {
            'address': address1
        }, function(results, status)        {
            if (status == google.maps.GeocoderStatus.OK)                 {
                //location of first address (latitude + longitude)
                location1 = results[0].geometry.location;
                document.getElementById("pickupLatitude").value = parseFloat(results[0].geometry.location.lat());
                document.getElementById("pickupLongitude").value = parseFloat(results[0].geometry.location.lng());

            } else {
                alert("Geocode was not successful for the following reason: " + status);
            }
        });
        geocoder.geocode( {
            'address': address2
        }, function(results, status)         {
            if (status == google.maps.GeocoderStatus.OK)                 {
                //location of second address (latitude + longitude)
                location2 = results[0].geometry.location;
                document.getElementById("dropLatitude").value = parseFloat(results[0].geometry.location.lat());
                document.getElementById("dropLongitude").value = parseFloat(results[0].geometry.location.lng());
                // calling the showMap() function to create and show the map 
                showMap();
            } else {
                alert("Geocode was not successful for the following reason: " + status);
            }
        });
    }
    return true;
}

// creates and shows the map
function showMap(){
    
    // show route between the points
    directionsService = new google.maps.DirectionsService();
    var request = {
        origin:location1, 
        destination:location2,
        travelMode: google.maps.DirectionsTravelMode.DRIVING
    };
    directionsService.route(request, function(response, status)     {
        if (status == google.maps.DirectionsStatus.OK)         {
            //alert(response.routes[0].legs[0].distance.value);
            document.getElementById("distanceInKM").value = (response.routes[0].legs[0].distance.value)/1000;
            distance = "The distance between the two points on the chosen route is: "+response.routes[0].legs[0].distance.text;
            distance += "<br/>The aproximative driving time is: "+response.routes[0].legs[0].duration.text;
        }
    });
}

//To Recalculate
function  initialize2(){
    geocoder = new google.maps.Geocoder(); // creating a new geocode object
		
    // getting the two address values
    address1 = document.getElementById("pickupStreetAddress1").value;
    address2 = document.getElementById("dropStreetAddress1").value;
    //alert("" + address1+address2);
    if(!address1 || (0 === address1.length) || ! address2 || (0 === address2.length)){
        return true;
    }
		
    // finding out the coordinates
    if (geocoder)     {
        geocoder.geocode( {
            'address': address1
        }, function(results, status)        {
            if (status == google.maps.GeocoderStatus.OK)                 {
                //location of first address (latitude + longitude)
                location1 = results[0].geometry.location;
                document.getElementById("pickupLatitude1").value = parseFloat(results[0].geometry.location.lat());
                document.getElementById("pickupLongitude1").value = parseFloat(results[0].geometry.location.lng());
            } else {
                alert("Geocode was not successful for the following reason: " + status);
            }
        });
        progress();
        geocoder.geocode( {
            'address': address2
        }, function(results, status)         {
            if (status == google.maps.GeocoderStatus.OK)                 {
                //location of second address (latitude + longitude)
                location2 = results[0].geometry.location;
                document.getElementById("dropLatitude1").value = parseFloat(results[0].geometry.location.lat());
                document.getElementById("dropLongitude2").value = parseFloat(results[0].geometry.location.lng());
                // calling the showMap() function to create and show the map 
                showMap2();
            } else {
                alert("Geocode was not successful for the following reason: " + status);
            }
        });
        progress();             
    }
    return true;
}

// creates and shows the map
function showMap2(){
    
    // show route between the points
    directionsService = new google.maps.DirectionsService();
    var request = {
        origin:location1, 
        destination:location2,
        travelMode: google.maps.DirectionsTravelMode.DRIVING
    };
    progress();
    directionsService.route(request, function(response, status)     {
        if (status == google.maps.DirectionsStatus.OK)         {
            //alert(response.routes[0].legs[0].distance.value);
            document.getElementById("distanceInKM1").value = (response.routes[0].legs[0].distance.value)/1000;
            distance = "The distance between the two points on the chosen route is: "+response.routes[0].legs[0].distance.text;
            distance += "<br/>The aproximative driving time is: "+response.routes[0].legs[0].duration.text;
        }
    });
    progress();
}

var prg_width = 200;
 
function progress() {
    var node = document.getElementById('progress');
    var w    = node.style.width.match(/\d+/);
 
    if (w >= prg_width) {
        w = 0;
    }
 
    node.style.width = parseInt(w) + 50 + 'px';
}
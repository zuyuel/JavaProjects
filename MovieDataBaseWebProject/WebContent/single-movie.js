function getParameterByName(target) {
    // Get request URL
	let url = window.location.href;//let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Uses regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {
    // populate the star info h3
    // find the empty h3 body by id "star_info"

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
    	let rowHTML = "";
    	rowHTML += "<tr>";
    	rowHTML += "<td>" + resultData[i]["id"] + "</td>";
        rowHTML += "<td>" + resultData[i]["title"] + "</td>";
        rowHTML += "<td>" + resultData[i]["year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["director"] + "</td>";
        var genre_list = resultData[i]["genre"].split(";");
        rowHTML += "<td><ul>";
        for(let i = 0; i < genre_list.length-1; i++)
        {
        	rowHTML += "<li>"+genre_list[i]+"</li>";
        }
        rowHTML += "</ul></td>";
        var star_list = resultData[i]["stars"].split(";");
        rowHTML += "<td><ul>";
        for(let i = 0; i < star_list.length-1; i++)
        {
        	rowHTML += '<li><a href="single-star.html?id='+star_list[i]+ '">'+star_list[i]+"</a></li>";
        }
        rowHTML += "</ul></td>";
        rowHTML += "<td>" + resultData[i]["rating"] + "</td>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let pattern = getParameterByName('movie');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?movie=" + pattern, // Setting request url, which is mapped by SingleMovieServlet in SingleMoviePage.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleMoviePage
});
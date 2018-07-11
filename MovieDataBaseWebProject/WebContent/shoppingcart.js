/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
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

    console.log("handleResult: populating cart info from resultData");

    // populate the cart info h3
    // find the empty h3 body by id "cart_info"
    let cartInfoElement = jQuery("#cart_info");

    // append two html <p> created to the h3 body, which will refresh the page
    cartInfoElement.append("<p>Movie ID: " + resultData[0]["movieId"] + "</p>" +
        "<p>Quantity: " + resultData[0]["customerId"] + "</p>");

//    console.log("handleResult: populating cart table from resultData");

    // Populate the cart table
    // Find the empty table body by id "movie_table_body"
//    let movieTableBodyElement = jQuery("#movie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
//    for (let i = 0; i < resultData.length; i++) {
//    	console.log("loop works");
//        let rowHTML = "";
//        rowHTML += "<tr>";
//        
//        rowHTML += 
//            "<td>" +
//            '<a href="single-movie.html?movie='+resultData[i]["movie_title"]+'">'
//            + resultData[i]["movie_title"] +
//            '</a>' +
//            "</td>";
//        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
//        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
//        rowHTML += "</tr>";
//
//        // Append the row created to the table body, which will refresh the page
//        movieTableBodyElement.append(rowHTML);
//    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let quantity = getParameterByName('quantity');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/cart?quantity=" + quantity, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});
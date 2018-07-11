
var past_query = [];
var past_suggested = {}; //make it into a jsonArray instead

function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated")
	
	// TODO: if you want to check past query results first, you can do it here
	if (past_query.indexOf(query)>-1)
	{
		console.log("Retrieving data from cache");
		var data = past_suggested[query];
		handleLookupAjaxSuccess(data, query, doneCallback);
	}
	else
	{
		console.log("sending AJAX request to backend Java Servlet")
		past_query.push(query);
		// sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
		// with the query data
		jQuery.ajax({
			"method": "GET",
			// generate the request url from the query.
			// escape the query string to avoid errors caused by special characters 
			"url": "api/Search?match=" + escape(query),
			"success": function(data) {
				// pass the data, query, and doneCallback function into the success handler
				handleLookupAjaxSuccess(data, query, doneCallback) 
			},
			"error": function(errorData) {
				console.log("lookup ajax error")
				console.log(errorData)
			}
		})
	}
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 * 
 * data is the JSON data string you get from your Java Servlet
 * 
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	
	// parse the string into JSON
	//var jsonData = JSON.parse(data); //might not have to parse to string
	console.log(data) // data is an array object
	// TODO: if you want to cache the result into a global variable you can do it here
	if (!past_suggested.hasOwnProperty(query)) //TODO check if the key exists
	{
		past_suggested[query] = data;
		console.log("showing past_suggestions")
		console.log(past_suggested)
	}
	// call the callback function provided by the autocomplete library
	// add "{suggestions: jsonData}" to satisfy the library response format according to
	//   the "Response Format" section in documentation
	doneCallback( { suggestions: data } ); //was jsonData
}


/*
 * This function is the select suggestion handler function. 
 * When a suggestion is selected, this function is called by the library.
 * 
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
	
	console.log("you select " + suggestion["value"])
	var url = "MovieList?movie=" + suggestion["data"]["movieID"]
	console.log("single-movie.html?movie="+suggestion["value"])
	//jump to the specific result page based on the selected suggestion
	window.location.href = "single-movie.html?movie="+suggestion["value"];
}


/*
 * This statement binds the autocomplete library with the input box element and 
 *   sets necessary parameters of the library.
 * 
 * The library documentation can be find here: 
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 * 
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
	// documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    // set the groupby name in the response json data field
    groupBy: "category",
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // added minimum characters
    minChars: 3,
    // Displays only 10 out of that many results
    lookuplimit: 10,
    
    preserveInput: false,
    
});


/*
 * do normal full text search if no suggestion is selected 
 */
function handleNormalSearch(query) {
	console.log("doing normal search with query: " + query);
	// TODO: you should do normal search here
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleNormalSearch($('#autocomplete').val())
	}
})

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button
/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */

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
function handleStarResult(resultData) {
    console.log("handleMovieResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += 
            "<td>" +
            // Add a link to index.html as a placeholder
            '<a href="single-movie.html?movie=' +resultData[i]["title"]+ '">'
            + resultData[i]["title"] +     // display star_name for the link text
            '</a>' +
            "</td>";
        rowHTML += "<td>" + resultData[i]["year"] + "</td>";
        rowHTML += "<td>" + resultData[i]["director"] + "</td>";
        //rowHTML += "<th>" + resultData[i]["genre"] + "</th>";
        var genre_list = resultData[i]["genre"].split(";");
        rowHTML += "<td><ul>";
        for(let i = 0; i < genre_list.length-1; i++)
        {
        	rowHTML += "<li>"+genre_list[i]+"</li>";
        }
        rowHTML += "</ul></td>";
        //rowHTML += "<th>" + resultData[i]["stars"] + "</th>";
        var star_list = resultData[i]["stars"].split(";");
        rowHTML += "<td><ul>";
        for(let i = 0; i < star_list.length-1; i++)
        {
        	//add star id
        	rowHTML += '<li><a href="single-star.html?id='+star_list[i]+ '">'+star_list[i]+"</a></li>";
        }
        rowHTML += "</ul></td>";
        rowHTML += "<td>" + resultData[i]["rating"] + "</td>";
        rowHTML += "</tr>";
        
        starTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
let url = "MovieList.html?";
let movie = getParameterByName('movie');
let numRecords = getParameterByName('numRecords');
let dropDown = getParameterByName('drop');
let genre = getParameterByName('genre');
let alphabet = getParameterByName('alphabet');
let sortBy = getParameterByName('sortBy');
let firstRecord = getParameterByName('firstRecord');

let filterHTML = "";
filterHTML +=	'<form id="mySearch" action="MovieList.html" method="GET">';                
filterHTML +=  	'<input type="text" name="numRecords" placeholder="items per page"/>';
if(numRecords==null)
{
	numRecords = "10";
}
if(firstRecord==null)
{
	firstRecord = "0";
}
if(movie!=null)
{
	filterHTML += '<input type="hidden" name="movie" value="'+movie+'">';
	url += 'movie='+movie+'&';
}
url += 'numRecords='+numRecords+'&';
if(dropDown!=null)
{
	filterHTML += '<input type="hidden" name="dropDown" value="'+dropDown+'">';
	url += 'drop='+dropDown+'&';
}
if(sortBy!=null)
{
	filterHTML += '<input type="hidden" name="sortBy" value="'+sortBy+'">';
	url += 'sortBy='+sortBy+'&';
}
if(genre!=null)
{
	filterHTML += '<input type="hidden" name="genre" value="'+genre+'">';
	url += 'genre='+genre+'&';
}
if(alphabet!=null)
{
	filterHTML += '<input type="hidden" name="alphabet" value="'+alphabet+'">';
	url += 'alphabet='+alphabet+'&';
}
filterHTML +=  	'<button type="SUBMIT" value="filter">filter</button>';
filterHTML +=  '</form>'; 

jQuery("#filter").append(filterHTML);

function handlePages(ItemCount){ // numRecords might be null
	console.log("handlePages: populating page numbers");
	var totalResults = parseInt(ItemCount["total"]);
	var currentPage = parseInt((parseInt(firstRecord)/parseInt(numRecords))+1,10);
	var totalPages = 0;
	if(totalResults%parseInt(numRecords)==0)
	{
		totalPages = (totalResults/parseInt(numRecords));
	}
	else
	{
		totalPages = parseInt((totalResults/parseInt(numRecords)) + 1,10);
	}
	// use jQuery("#pages").append()
	let pageHTML = "";
	let leftURL = url;
	let rightURL = url;
	var left = Math.max(0,parseInt(firstRecord)-parseInt(numRecords));
	var right = Math.min((totalPages*parseInt(numRecords))-parseInt(numRecords),parseInt(firstRecord)+parseInt(numRecords));
	pageHTML += '<a href="'+leftURL+'firstRecord='+left.toString()+'&">&laquo;</a>'; // TODO check MovieList.html url
	
	if(totalPages<=7)
	{
		console.log(currentPage);
		for(let i = 1;i<=totalPages;i++)
		{
			let baseURL = url;
			if(currentPage==i)
			{
				
				pageHTML += '<a id="current" href="'+baseURL+'">'+i.toString()+'</a>';
			}
			else
			{
				var first = (i-1)*parseInt(numRecords);
				baseURL += 'firstRecord='+first.toString()+'&';
				pageHTML += '<a href="'+baseURL+'" >'+i.toString()+'</a>'; // TODO change url
			}
		}
	}
	else
	{
		
	}
	
	pageHTML += '<a href="'+rightURL+'firstRecord='+right+'&">&raquo;</a>'; //TODO check MovieList.html url
	jQuery(".container").append(pageHTML);
}

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/MovieList?movie="+movie+"&numRecords="+numRecords+"&firstRecord="+firstRecord+"&sortBy="+sortBy+"&drop="+dropDown+"&genre="+genre+"&alphabet="+alphabet, // was url: "/api/MovieList" Setting request url, which is mapped by StarsServlet in SingleMovie.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the SingleMovie
});

// Makes HTTP GET request to Pagination servlet and populates the page number
jQuery.ajax({
	dataType: "json",
	method: "GET",
	url: "api/Pagination?movie="+movie+"&drop="+dropDown+"&sortBy="+sortBy+"&genre="+genre+"&alphabet="+alphabet,
	success: (ItemCount) => handlePages(ItemCount)
});




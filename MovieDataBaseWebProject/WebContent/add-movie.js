/**
 * http://usejsdoc.org/
 */
/*
function handleAddMovie(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle movie exist");
    console.log(resultDataJson);
    console.log(resultDataJson["exist"]);

    // If login success, redirect to index.html page
    // TODO fix display error message
    if (resultDataJson["exist"] === "success") {
    	console.log("show failure message");
    	jQuery("#add_movie_error_message").text(resultDataJson["message"]);
    }
    // If login fail, display error message on <div> with id "login_error_message"
    // TODO fix display message
    else {
        console.log("show success message");
        console.log(resultDataJson["message"]);
        jQuery("#add_movie_error_message").text(resultDataJson["message"]);
    }
}*/

// Bind the submit action of the form to a handler function
function handleAddMovie(resultDataString) {
        //resultDataJson = JSON.parse(resultDataString);

        console.log("handle movie exist");
        console.log(resultDataString);
        console.log(resultDataString["exist"]);

        // If login success, redirect to index.html page
        // TODO fix display error message
        if (resultDataString["exist"] === "success") {
        	console.log("show failure message");
        	alert(resultDataString["message"]);
        }
        // If login fail, display error message on <div> with id "login_error_message"
        // TODO fix display message
        else {
            console.log("show success message");
            console.log(resultDataString["message"]);
            alert(resultDataString["message"]);
        }
}

jQuery("#add_movie_form").submit(function submitMovieForm(formSubmitEvent) {
    console.log("submit add movie form");

    // Important: disable the default action of submitting the form
    //   which will cause the page to refresh
    //   see jQuery reference for details: https://api.jquery.com/submit/
    formSubmitEvent.preventDefault();

    var title = $('#title').val();
    var year =  $('#year').val();
    var director =  $('#director').val();
    var star =  $('#star').val();
    var genre =  $('#genre').val();
    var rating =  $('#rating').val();
    
    console.log(title);
    console.log(year);
    console.log(director);
    console.log(star);
    console.log(genre);
    console.log(rating);
    

    //ajax request was here
    jQuery.ajax({
    	dataType: "json",
    	method: "GET",
    	url: "api/AddMovie?title="+title+"&year="+year+"&director="+director+"&star="+star+"&genre="+genre+"&rating="+rating,
    	success: (resultDataString) => handleAddMovie(resultDataString)
    });

}); //(event) => submitMovieForm(event)


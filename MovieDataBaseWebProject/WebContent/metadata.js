/**
 * http://usejsdoc.org/
 */

function handleTables(ResultData)
{
	console.log("Filling up all database table");
	RowHTML = "";
	//RowHTML = "<thead>";
	let MetadataTableBodyElement = jQuery("#metadata_table_body");
	for(let i = 0; i < ResultData.length; i++)
		{
			RowHTML += "<tr><td>"+ResultData[i]["table_name"]+"</td>";
			RowHTML += "<td>"+ResultData[i]["elements"]+"</td></tr>";
		}
	//RowHTML +="<thead>";
	MetadataTableBodyElement.append(RowHTML);
}

jQuery.ajax({
	dataType: "json",
	method: "GET",
	url: "api/MetaData",
	success: (ResultData) => handleTables(ResultData)
});
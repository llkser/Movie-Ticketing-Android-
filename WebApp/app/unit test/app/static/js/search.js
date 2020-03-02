$(document).ready(function() {
 $("input").keyup(function() {


        $.ajax({

           url: '/respond',

           type: 'POST',

           data: JSON.stringify({ response: document.getElementById("ds").value }),

contentType: "application/json; charset=utf-8",

dataType: "json",

success: function(response){
document.getElementById('d1').innerHTML=response.response
},

error: function(error){
  console.log(error);
}
});
});
})

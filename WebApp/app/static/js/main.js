function test(obj)
{
var i=obj.getAttribute("id");
$.ajax({
   url: '/respond2',
   type: 'POST',
   data: JSON.stringify({ response: obj.getAttribute("id") }),
   contentType: "application/json; charset=utf-8",
   dataType: "json",
   success: function(response){
   if(obj.innerHTML=='finished'){
  obj.innerHTML='unfinished';

  obj.style.color='red';
}
else if(obj.innerHTML=='unfinished'){
  obj.innerHTML='finished';
  obj.style.color='blue';
}},
error: function(error){
console.log(error);
}
});
};
function test2(obj)
{
var i=obj.getAttribute("id");
$.ajax({
   url: '/respond2',
   type: 'POST',
   data: JSON.stringify({ response: obj.getAttribute("id") }),
   contentType: "application/json; charset=utf-8",
   dataType: "json",
   success: function(response){
   if(obj.innerHTML=='finished'){
  obj.innerHTML='unfinished';

  obj.style.color='red';
}
else if(obj.innerHTML=='unfinished'){
  obj.innerHTML='finished';
  obj.style.color='blue';
}},
error: function(error){
console.log(error);
}
});
};
function test3()
{


};
function test4()
{
  document.getElementById("d2").style.visibility="hidden"
};
function test5()
{
document.getElementById("d2").style.visibility="visible"
$.ajax({
   url: '/respond3',
   type: 'POST',
   data: JSON.stringify({ response: obj.getAttribute("id") }),
   contentType: "application/json; charset=utf-8",
   dataType: "json",
   success: function(response){

}},
error: function(error){
console.log(error);
}
});
};

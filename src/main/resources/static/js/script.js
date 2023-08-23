console.log("my name is mart");


const toggleSidebar=()=>{
	
	if($(".sidebar").is(":visible")){
	//band karna h	
	
	$(".sidebar").css("display","none");
	$(".content").css("margin-left","0%");
	}
	else{
		//show krna hai
		$(".sidebar").css("display","block")
		$(".content").css("margin-left","20%");
	}
};
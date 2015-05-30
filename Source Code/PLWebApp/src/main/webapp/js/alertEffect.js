$(function() {
        $(document).ready(function() {
                function fadeInOut(){
                        $('.needHelp').fadeOut(500, function(){
                                $('.needHelp').fadeIn(500, function(){
                                        setTimeout(fadeInOut, 300);
                                });
                        });
                }
                fadeInOut();
				
				if($("#detail").length) {
                        var gradeDetail = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
                        $('.manualGrade').each(function() {
                                if($(this).val() == 100)
                                        gradeDetail[10]++;
                                else if($(this).val() >= 90)
                                        gradeDetail[9]++;
                                else if($(this).val() >= 80)
                                        gradeDetail[8]++;
                                else if($(this).val() >= 70)
                                        gradeDetail[7]++;
                                else if($(this).val() >= 60)
                                        gradeDetail[6]++;
                                else if($(this).val() >= 50)
                                        gradeDetail[5]++;
                                else if($(this).val() >= 40)
                                        gradeDetail[4]++;
                                else if($(this).val() >= 30)
                                        gradeDetail[3]++;
                                else if($(this).val() >= 20)
                                        gradeDetail[2]++;
                                else if($(this).val() >= 10)
                                        gradeDetail[1]++;
                                else if($(this).val() == 0)
                                        gradeDetail[0]++;
                        });
                        var scoreStandard = 0;
                        for(var i = 0; i < gradeDetail.length; i++) {
                                $("#detail").append(scoreStandard + "分: " + gradeDetail[i] + "人　");
                                scoreStandard += 10;
                        }
                }
        });
		
		$('#finalGrade').click(function(){
                var finalColumnStr = "";
                var count = 0;
                $(".finalColumn").each(function(){
                        if(count % 2 == 1) {
                                var grade = Math.round($(this).html());
                                finalColumnStr += "     " + grade;
                        } else
                               finalColumnStr +=$(this).html();
                        if(count % 2 == 1)
                                finalColumnStr += "<br>";
                        count++;
                });
		});
		
		$('#finalGrade').click(function(){
                var finalColumnStr = "";
                var count = 0;
                $(".finalColumn").each(function(){
                        if(count % 2 == 1) {
                                var grade = Math.round($(this).html());
                                finalColumnStr += " " + grade;
                        } else
                               finalColumnStr += " " + $(this).html();
                        if(count % 2 == 1)
                                finalColumnStr += "<br>";
                        count++;
                });

                $('#finalGradeColumn').html(finalColumnStr);
        });

        $(".finalGradeInput").on("keyup", function(){
                if(!$.isNumeric($(this).val()))
                        return;
                var tr = $("#reportTable tr.start");
                var trCount = 1;
                tr.each(function() {
                        var td = $(this).find("td.columnGrade");
                        var sum = 0;
                        var average = 0;
                        td.each(function() {

                                $(this).attr('param');
                                sum += (parseFloat($(this).html())) * $("#input_"+$(this).attr('param')).val() / 100;

                        });
                        average = sum * $("#average").val() / 100;
                        $(this).find(".stuAverage").html(average.toFixed(2));
                        trCount++;
                });
        });
		
		$("#hideInfo").click(function(){
                $(".stuInfo").hide();
                $("#showInfo").show();
                $(this).hide();
        });

        $("#showInfo").click(function(){
                $(".stuInfo").show();
                $("#hideInfo").show();
                $(this).hide();
        });

		$(".submitStuGrade").click(function(){
                var classId = $(this).attr('classId');
                var courseId = $(this).attr('courseId');
                var lessonId = $(this).attr('lessonId');
                var userId = $(this).attr('userId');

                var manualGrade = $(this).parent().find(".manualGrade").val();
                $.ajax({
                        url: '/dataSetting.groovy',
                        dataType:'text',
                        type:'GET',
                        data:{
                                action: 'updateManualGrade',
                                classId: classId,
                                courseId: courseId,
                                lessonId: lessonId,
                                userId: userId,
                                manualGrade: manualGrade
                        },
                        success: function(response){
                                alert("儲存成功");
                        }
                });
        });
		$("._hideInfo").click(function(){
                $(".hideTd").attr('colspan', 2);
        });
        $("._showInfo").click(function(){
                $(".hideTd").attr('colspan', 4);
        });
		
		$("input").on("keyup", function(){
                var tr = $("#reportTable tr.start");
                tr.each(function() {
                        var td = $(this).find("td.stuAverage");
                        var grade = td.html();
                        if(grade < 60)
                                td.css("color", "red");
						else
								td.css("color", "black");

                });
        });

        $("#adjustGrade, #gradeAdujust").on("keyup", function(){
                if(!$.isNumeric($(this).val()))
                        return;
                var gradePlus = parseFloat($(this).val());
                var tr = $("#reportTable tr.start");
                tr.each(function() {
                        var td = $(this).find("td.stuAverage");
                        var grade = parseFloat(td.html());
                        if(grade < 60) {
                                if(grade + gradePlus > 60)
                                        td.html("60");
                                else
                                        td.html(grade + gradePlus);
                        }
                });

        });
		
});
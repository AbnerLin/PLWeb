$(function() {
        $(document).ready(function() {
         // alert("hello");

        });

        $("#autoSet").click(function() {
//              alert("123");

                var totalPercent = 100/$(".totalGrade").size();
                $(".totalGrade").val(totalPercent.toFixed(1));

                $(".paramClass").each(function(){
                        var totalParam = $(this).find("input").size();
                        var percent = 100/totalParam;
                        $(this).find("input").val(percent.toFixed(1));
                });
        });

        $("#gradeSubmit").click(function(){
        //      alert(123);

                var result = {};
                var count = 1;
                var _count = 1;
                var obj;
                $(".paramClass").each(function(){
                        obj = {};
                        _count = 1;
                        $(this).find("input").each(function(){
                                obj[_count] = parseFloat($(this).val());
                                _count++;
                        });

                        result[count] = obj;
                        count++;
                });
//              alert(JSON.stringify(result));

                var totalPercent = {};
                count = 1;
                $(".totalGrade").each(function(){
                        totalPercent[count] = parseFloat($(this).val());
                        count++;
                });
//              alert(JSON.stringify(totalPercent));

                $.ajax({
                        url: '/dataSetting.groovy',
                        dataType:'text',
                        type:'GET',
                        data:{
                                action: 'setGrade',
                                gradeSet: JSON.stringify(result),
                                totalSet: JSON.stringify(totalPercent),
                                courseId: course_id,
                                lessonId: lesson_id
                        },
                        success: function(response){
                                alert("儲存成功");
                        }
                });

        });

});

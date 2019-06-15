function doDomainBatch(obj){
    var form = $(obj).closest("form");
    var checkList = $("input[type='checkbox'][name='fileKey']:checked")
    if(checkList.length==0){
        alert("请选择左侧需要操作的domain对象")
        return null;
    }
    var fileKeys = "";
    checkList.each(function () {
        fileKeys = fileKeys+","+$(this).val();
    })
    fileKeys = fileKeys.substring(1);
    form.find("input[name='fileKeys']").val(fileKeys)

    $.ajax({
        url:form.attr("action"),
        method:"POST",
        data:form.serializeArray(),
        success:function(result){
            alert(result.message);
            if(result.result == "SUCCESS"){
                //清除input中的值
                form.find("input[type='text']").val('')
                //清除textarea
                form.find("textarea").html('')
            }
        },
        error:function(jqXHR){
            alert("请求出错");
        }
    });
}

function doAjaxSubmit(url,array,refresh){
    $.ajax({
        url:url,
        data:array,
        method:"POST",
        success:function(result){
            if(result.message!='')
                alert(result.message)
            if(result.result == "SUCCESS" && refresh){
                location.href=location.href
            }
        },
        error:function(jqXHR){
            alert("请求出错");
        }
    });
}

function doAjaxFormSubmit(form,refresh){

    $.ajax({
        url:$(form).attr("action"),
        data:$(form).serializeArray(),
        method:"POST",
        success:function(result){
            if(result.message!='')
                alert(result.message)
            if(result.result == "SUCCESS" && refresh){
                location.href=location.href
            }
        },
        error:function(jqXHR){
            alert("请求出错");
        }
    });
}
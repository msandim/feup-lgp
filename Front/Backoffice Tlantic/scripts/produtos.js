//GLOBALS

//Servidor e serviços
var configs={
  server:"http://intelligentsalesguide.me:9000/",
  getAllCategories:"api/allCategories",
  productsByCategory: "api/productsByCategory?code=",
  addCategoryApi :"api/addCategory", //?name=x&code=   -> POST
  removeCategoryApi : "api/removeCategory", //code = Código da categoria a remover    -> DELETE
  addProducts : "api/addProducts", //code  E  products (ficheiro csv)  -> POST
  deleteProducts : "api/removeProducts" //category (codigo da cat)  products (array dos produtos a serem removidos)
}

//arrays
var CategoryArray = [];
var CodeCategoryArray = [];
var ProductsArray= [];
var ProductEarArray = [];
var removeCategoryArr = [];
var removeProductsArr = [];

function expandProducts() {
  var self = this;
  $('.expandProducts').click(function(){
    var boxParent = $( this ).closest(".row");
    var boxBody = boxParent.children(".box-of-products");
    if(boxBody.is(":visible")){
      $(boxBody).hide('fast');
    }else{
      $(boxBody).show('medium');
    }

    var span = $(this).find(">:first-child");
    var spanClass = span.attr("class");
    if(spanClass == "glyphicon glyphicon-menu-up"){
      span.attr("class","glyphicon glyphicon-menu-down");
    }else{
      span.attr("class","glyphicon glyphicon-menu-up");
    }
  });
}

function addCategory() {
  var self = this;
  $('.addCategory').click(function(){

    var categoryName = $('.inputCatName').val();
    var categoryCode = $('.inputCatCode').val();
    
   /* var CategoryExists = [];
    var nameCategory = false;

    for (var i = 0; i < ProductsArray.length; i++) {
      CategoryExists.push(ProductsArray[i].category);
    }

    if($('.inputCatName').hasClass('border')){
       $('.inputCatName').removeClass('border');
    }

    while(!nameCategory){
      for (var j = 0; j < CategoryExists.length; j++) {
        if (CategoryExists[j] === capitalizeFirstLetter(categoryName)) {
          nameCategory=false;
          $('.inputCatName').addClass("border");

          //edit value placeholder
          $('.inputCatName').val('');
          $('.inputCatName').attr("placeholder", "Nome da Categoria ja existe");
          //disable button submit add category
          $('#InputButtonCatName').attr("disabled", true);
          $('#myBtn').click();
          return false;
        }
        else {
          nameCategory=true;
        }
      }
    }*/


    $.ajax({
        url: configs.server+configs.addCategoryApi,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({name: categoryName , code: categoryCode}),
        type: "POST",
        crossDomain: true,
    })
    
    //add array
    CategoryArray.push(categoryName);
    CodeCategoryArray.push(categoryCode);

    var boxGroup = $('#boxOfGoodies');

    categoryName = capitalizeFirstLetter(categoryName);
    //boxGroup.append('<div class="box"><div class="box-header"><div class="col-sm-5 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size"><h3>'+categoryName+'</h3></div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button"class="btn btn-block product-button addProduct">Adicionar Produtos</button> </div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button expandProducts">Lista de Produtos <span class="glyphicon glyphicon-menu-down"></span></button></div> <div class="col-sm-1 table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button removeCategory"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="inputFile" style="display:none"> <div ><label class="btn btn-primary col-sm-11 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size" for="my-file-selector"><input id="my-file-selector" name="file" type="file" style="display:none;"> <!--accept=".csv"--><h4>Escolher ficheiro</h4></label><button type="submit" form="form1" value="Submit" class="col-sm-1 table-col-border table-col-border-top contain-button col-size btn btn-danger addFile">Submeter</button></div></div><div class="box-body box-body-scroll"></div></div>');
    boxGroup.append('<div class="box"><div class="row"><div class="col-sm-5 table-col-border-first table-col-size table-contain-h3"><h3>'+categoryName+'</h3></div><div class="col-sm-3 text-center table-col-border-first table-col-size table-contain-button"><button type="button"class="btn btn-block table-button addProduct">Adicionar Produtos</button> </div><div class="col-sm-3 text-center table-col-border-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandProducts">Lista de Produtos <span class="glyphicon glyphicon-menu-down"></span></button></div><div class="col-sm-1 table-col-border-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeCategory"><span class="glyphicon glyphicon-remove"></span></button></div> <div class="inputFile" style="display:none"> <div ><label class="btn btn-primary col-sm-11 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size" ><input class="my-file-selector" name="file" type="file" style="display:none;"> <!--accept=".csv"--><h4>Escolher ficheiro</h4></label></div></div><div class="box-of-products"><div class="container-fluid"></div></div></div><div>');

  

    //Unbind all elements with the class and then rebbind to include the new element
      $(".expandProducts").unbind("click", expandProducts());
      $(".expandProducts").bind("click", expandProducts());
      $(".removeCategory").unbind("click", removeCategory());
      $(".removeCategory").bind("click", removeCategory());
      $(document).unbind("ready");
      $(document).bind("ready", function () { $(".my-file-selector").change(function(){var FileName = $(this).val().split('\\').pop(); $(this).parent().find('h4').text(FileName);});  $(document).on('click','.addProduct',function(){var boxParent = $( this ).parent().parent();var boxInputFile = boxParent.next(); if(boxInputFile.is(":visible")){$(boxInputFile).hide('fast'); }else{$(boxInputFile).show('medium');}var DefaultName = boxParent.parent().find('h4').text("Escolher ficheiro"); }); $(document).on('change', '.my-file-selector', function() {var FileName = $(this).val().split('\\').pop(); $(this).parent().find('h4').text(FileName); });   $(document).on('click', '.saveChanges', function() { for(var c=0; c < removeCategoryArr.length; c++) { $.ajax({ url: configs.server+configs.removeCategoryApi+'?code='+removeCategoryArr[c], type: 'DELETE', crossDomain: true, success: function(result) {} });}}); });
              
      //clear placeholder
      $('.inputCatName').val('');
      $('.inputCatName').attr("placeholder", ">");

      //disable button submit add category
      $('#InputButtonCatName').attr("disabled", true);
  });

}

function autoAddCategory(){
  $(document).ready(function(){
    $.ajax({
        url: configs.server+configs.getAllCategories,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        type: "GET",
        crossDomain: true,
      }).done(function(response){
        loadFinished();
        var i, j;

        for(i=0;i<response.length;i++)
          for(j in response[i]) {
            console.log("property name: " + j,"value: "+response[i][j]);

            if(!j.localeCompare("name")) {
              var self = this;
              var boxGroup = $('#boxOfGoodies');

              //boxGroup.append('<div class="box"><div class="box-header"><div class="col-sm-5 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size"><h3>'+categoryName+'</h3></div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button"class="btn btn-block product-button addProduct">Adicionar Produtos</button> </div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button expandProducts">Lista de Produtos <span class="glyphicon glyphicon-menu-down"></span></button></div> <div class="col-sm-1 table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button removeCategory"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="inputFile" style="display:none"> <div ><label class="btn btn-primary col-sm-11 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size" for="my-file-selector"><input id="my-file-selector" name="file" type="file" style="display:none;"> <!--accept=".csv"--><h4>Escolher ficheiro</h4></label><button type="submit" form="form1" value="Submit" class="col-sm-1 table-col-border table-col-border-top contain-button col-size btn btn-danger addFile">Submeter</button></div></div><div class="box-body box-body-scroll"></div></div>');
             boxGroup.append('<div class="box"><div class="row"><div class="col-sm-5 table-col-border-first table-col-size table-contain-h3"><h3>'+response[i][j]+'</h3></div><div class="col-sm-3 text-center table-col-border-first table-col-size table-contain-button"><button type="button"class="btn btn-block table-button addProduct">Adicionar Produtos</button> </div><div class="col-sm-3 text-center table-col-border-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandProducts">Lista de Produtos <span class="glyphicon glyphicon-menu-down"></span></button></div><div class="col-sm-1 table-col-border-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeCategory"><span class="glyphicon glyphicon-remove"></span></button></div> <div class="inputFile" style="display:none"> <div ><label class="btn btn-primary col-xs-12  col-lg-11 col-md-11 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size" ><input class="my-file-selector" name="file" type="file" style="display:none;"> <!--accept=".csv"--><h4>Escolher ficheiro</h4></label></div></div><div class="box-of-products"><div class="container-fluid"></div></div></div><div>');
              CategoryArray.push(response[i][j]);

              //Unbind all elements with the class and then rebbind to include the new element
              $(".expandProducts").unbind("click", expandProducts());
              $(".expandProducts").bind("click", expandProducts());
              $(".removeCategory").unbind("click", removeCategory());
              $(".removeCategory").bind("click", removeCategory());
              $(document).unbind("ready");
              $(document).bind("ready", function () { $(".my-file-selector").change(function(){var FileName = $(this).val().split('\\').pop(); $(this).parent().find('h4').text(FileName);});  $(document).on('click','.addProduct',function(){var boxParent = $( this ).parent().parent();var boxInputFile = boxParent.next(); if(boxInputFile.is(":visible")){$(boxInputFile).hide('fast'); }else{$(boxInputFile).show('medium');}var DefaultName = boxParent.parent().find('h4').text("Escolher ficheiro"); }); $(document).on('change', '.my-file-selector', function() {var FileName = $(this).val().split('\\').pop(); $(this).parent().find('h4').text(FileName); });   $(document).on('click', '.saveChanges', function() { for(var c=0; c < removeCategoryArr.length; c++) { $.ajax({ url: configs.server+configs.removeCategoryApi+'?code='+removeCategoryArr[c], type: 'DELETE', crossDomain: true, success: function(result) {} });}}); });
              
               var lastChild = boxGroup.find(">:last-child");
              //console.log("OLA " + lastChild.attr('class') );
              
            }
            else {
               CodeCategoryArray.push(response[i][j]);
              autoAddProducts(response[i][j], lastChild);
            }
           
          }
            
        console.log(CodeCategoryArray); 
        console.log(CategoryArray);
      }).fail(function(err){
        loadFinished();
        processError(err);
      });

   
  });

}

function autoAddProducts(categoryName,lastChild) {
  var self = this;
  var boxProducts = lastChild.find(".box-of-products").find(".container-fluid");
  for (var i = 0; i < CodeCategoryArray.length; i++) {
      //console.log(CodeCategoryArray[i] + " PRODUTOS " +  categoryName);
      if(categoryName === CodeCategoryArray[i]){
    
        $.ajax({
                url: configs.server+configs.productsByCategory+CodeCategoryArray[i],
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                type: "GET",
                crossDomain: true,
              }).done(function(response){
                loadFinished();
                var i, j;

                for(i=0;i<response.length;i++)
                  for(j in response[i]) {
                    if(!j.localeCompare("name")) {
                     //console.log("property name: " + j,"value: "+response[i][j]);
                     ProductsArray.push(response[i][j]);

                    boxProducts.append('<div class="row"><div class="col-sm-10 table-col-border-first table-col-size nameProducts"> '+response[i][j]+' </div> <div class="col-sm-1 table-col-border-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeProduct"><span class="glyphicon glyphicon-remove"></span></button></div></div>');
                    $(".removeProduct").unbind("click", removeProduct());
                    $(".removeProduct").bind("click", removeProduct());
                    }
                    else if (!j.localeCompare("ean")) {
                      //console.log(response[i][j]);
                      ProductEarArray.push(response[i][j]);
                    }
                  }
               }).fail(function(err){
            loadFinished();
            processError(err);
          });   
        } 
  }

}


//check input is empty and disable button submit
$(document).ready(function() {
    $('.modal-body input').keyup(function() {

        var empty = false;
        $('.modal-body input').each(function() {
            if ($(this).val().length == 0) {
                empty = true;
            }
        });

        if (empty) {
            $('#InputButtonCatName').attr('disabled', 'disabled');
        } else {
            $('#InputButtonCatName').attr('disabled', false);
        }
    });

     $(document).on('click','.addProduct',function(){
       var boxParent = $( this ).parent().parent();
       var boxInputFile = boxParent.find(".inputFile");

       if(boxInputFile.is(":visible")){
        $(boxInputFile).hide('fast');
       }else{
        $(boxInputFile).show('medium');
        }

      var DefaultName = boxParent.parent().find('h4').text("Escolher ficheiro");
    });

     $(document).on('change', '.my-file-selector', function() {
      var FileName = $(this).val().split('\\').pop();
      $(this).parent().find('h4').text(FileName);
     });

      $(document).on('click', '.saveChanges', function() {
        //REMOVER CATEGORIA
        for(var c=0; c < removeCategoryArr.length; c++) {
          $.ajax({
              url: configs.server+configs.removeCategoryApi+'?code='+removeCategoryArr[c],
              type: 'DELETE',
              crossDomain: true,
              success: function(result) {
                  // Do something with the result
                  removeCategoryArr.length = 0;
              }
          });
        }

        //remover produtos 
        for(var c=0; c < CodeCategoryArray.length; c++) {
          var arrayproducts=[];

            for(var i=0; i < removeProductsArr.length; i++) {              
              if(removeProductsArr[i][0] === CodeCategoryArray[c]) {
                console.log("LOL " + removeProductsArr.length + "  " + ProductsArray.length);

                  for(var x=0; x < ProductsArray.length; x++) {

                    var nomePro = removeProductsArr[i][1].trim();
                    var nomeProd = ProductsArray[x].trim();
                    //console.log("PEA " + nomePro+ " E " + nomeProd);
                    if(nomePro === nomeProd) {
                      //console.log("AQUIIII");
                      arrayproducts.push(ProductEarArray[x]);
                    }

                  }   

              }
            }

            
            //console.log("FINAL " +  CodeCategoryArray[c] + " EEE " + arrayproducts + "  EARNS  " + ProductEarArray.length);
            if(arrayproducts.length != 0) {
               $.ajax({
                url: configs.server+configs.deleteProducts,
                type: 'DELETE',
                data: JSON.stringify({category: CodeCategoryArray[c], products: arrayproducts}),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                crossDomain: true,
                success: function(result) {
                    // Do something with the result
                    removeProductsArr.length = 0;
                }
               });
           }
        }
          
        // adicionar produtos
        $(".my-file-selector").each(function() {
            var self = $(this);
            var filename = self.val().split('\\').pop();
            var myFile = self.prop('files');

            console.log(myFile);

            if(filename.length !== 0) {
              var fatherInputFile = self.parent().parent().parent().parent();
              var nameCategory = fatherInputFile.find(">:first-child").find('h3').text();

              var index;
              for (var i = 0; i < CategoryArray.length; i++) {
                if(nameCategory === CategoryArray[i]) {
                  index = i;
                  break;
                }
              }

              var code = CodeCategoryArray[index];
             
              $.ajax({
                  url:  configs.server+configs.addProducts,
                  type: 'POST',
                  data: {code: code, csv: myFile},
                  dataType : 'json',
                  contentType: false,
                  processData: false,
                  success: function (data) {
                       console.log("SUCESSO   ADD PRODUCTS");
                  },
              });
            
              console.log(filename + " E " +nameCategory + " C " + code);
            }
        });
        
    });

});


function removeProduct() {

  $('.removeProduct').on('click', function(){

    var currentRow = $(this).closest(".row");
    var nameCategory = currentRow.parent().closest(".row").find(">:first-child").find("h3").text();
    var product = currentRow.find('.nameProducts').text();
    //console.log("Cate " + nameCategory.find('h3:first').html() + " PROD " + product);

    var index;
    for (var i = 0; i < CategoryArray.length; i++) {
      if(nameCategory === CategoryArray[i]) {
        index = i;
        break;
      }
    }

    removeProductsArr.push([CodeCategoryArray[index] , product]);
    console.log(removeProductsArr);
  
    currentRow.remove();
  });
}

function removeCategory() {
   var self = this;
  $('.removeCategory').on('click', function(){

     var currentBox = $(this).closest(".box");
     var categoryName = currentBox.find(">:first-child").find(">:first-child").find('h3').text();
     var index;
      for (var i = 0; i < CategoryArray.length; i++) {
        if(categoryName === CategoryArray[i]) {
          index = i;
          break;
        }
      }
    removeCategoryArr.push(CodeCategoryArray[index]);
    // console.log(CodeCategoryArray[index]);
    
     currentBox.remove();
  });
}

function AddModal() {
  // Get the modal
  var modal = document.getElementById('myModal');

  // Get the button that opens the modal
  var btn = document.getElementById("myBtn");

  // Get the <span> element that closes the modal
  var span = document.getElementsByClassName("close")[0];

  // When the user clicks the button, open the modal
  btn.onclick = function() {
      modal.style.display = "block";
  }

  // When the user clicks on <span> (x), close the modal
  span.onclick = function() {
       if($('.inputCatName').hasClass('border')){
         $('.inputCatName').removeClass('border');
       }
       //edit placeholder
      $('.inputCatName').val('');
      $('.inputCatName').attr("placeholder", ">");
      //disable button submit add category
      $('#InputButtonCatName').attr("disabled", true);
      modal.style.display = "none";
  }

  // When the user clicks anywhere outside of the modal, close it
  window.onclick = function(event) {
      if (event.target == modal) {
           if($('.inputCatName').hasClass('border')){
               $('.inputCatName').removeClass('border');
          }
          //edit placeholder
          $('.inputCatName').val('');
          $('.inputCatName').attr("placeholder", ">");
          //disable button submit add category
          $('#InputButtonCatName').attr("disabled", true);
          modal.style.display = "none";
      }
  }

  $('.addCategory').click(function(){
      modal.style.display = "none";
  });

}


//UTILS
function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}


function loadFinished(){
  $('#loady').remove();
  $("body").find("*").removeAttr("disabled");
  $('.interface-square').children().show();
}

function processError(err){
  var msg = err;
  msg = JSON.parse(msg['responseText']);
  alert(msg['msg']);
}
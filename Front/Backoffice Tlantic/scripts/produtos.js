//GLOBALS

/*$.getJSON("/some/url", function(data) { 
    // Now use this data to update your view models, 
    // and Knockout will update your UI automatically   
})*/

//Trocar por serviço
  var ProductsArray =
   [
     {
       "category":"Televisão",
        "products":[
  
            {"name":"TV LED Smart TV3D 42'' LG 42LF652V"},
            {"name":"TV LED Smart TV3D 42'' LG 43LF652V"},
            {"name":"TV LED Smart TV3D 42'' LG 44LF652V"},
            {"name":"TV LED Smart TV3D 42'' LG 45LF652V"}
          
        ]
      },
      {
       "category":"Computador",
        "products":[
          
            {"name":"TV LED Smart TV3D 42'' LG 52LF652V"},
            {"name":"TV LED Smart TV3D 42'' LG 53LF652V"},
            {"name":"TV LED Smart TV3D 42'' LG 54LF652V"},
            {"name":"TV LED Smart TV3D 42'' LG 55LF652V"}
          
        ]
      }
    ];

   Products=ko.observableArray();

function filter(){

  var ViewModel = function() {
    var self = this;

      self.filter = ko.observable('');

      /*self.items = ko.observableArray(["Televisão", "Computador", "Micro-ondas", "Frigorífico", "Telemóvel", "Colunas", "Rádio",
        "Televisão", "Computador", "Frigorífico", "Telemóvel", "Colunas", "Rádio", "Televisão", "Computador", "Micro-ondas", "Frigorífico",
         "Telemóvel", "Colunas", "Rádio", "Televisão", "Computador", "Frigorífico", "Telemóvel", "Colunas", "Rádio"]);

      self.items.sort();*/

      self.filteredItems = ko.computed(function() {
      var filter = self.filter().toLowerCase();
        if(!filter) { return Products(); }
        return Products().filter(function(i) {
          return i.toLowerCase().indexOf(filter) > -1;
        });
      });
    };

    ko.applyBindings(new ViewModel());

}

function expand() {
  var self = this;
  $('.expandProducts').click(function(){
    var boxParent = $( this ).parent().parent().parent();
    var boxBody = boxParent.children(".box-body");
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

function addProduct(){
  var self = this;
  $('.addProduct').click(function(){
     
    var boxParent = $( this ).parent().parent();
    var boxInputFile = boxParent.next();
    if(boxInputFile.is(":visible")){
      $(boxInputFile).hide('fast');
    }else{
      $(boxInputFile).show('medium');
    }

    var DefaultName = boxParent.parent().find('h4').text("Escolher ficheiro");
  });
}

function addFile() {
   //add produtos a uma categoria
  $('.btn-danger').click( function(){
      $(this).parent().parent().hide('fast');    
  
  var fatherInputFile = $(this).parent().parent().parent();
  var nameCategory = fatherInputFile.find(">:first-child").find('h3').text();

  var filename = $('#my-file-selector').val().split('\\').pop();
  var myFile = $('#my-file-selector').prop('files');

  //console.log("OLA " + fatherInputFile.attr('class') + " " + filename +"  " + nameCategory);
  
  $.ajax({
    url: '/api/addc/',
    data: {name: nameCategory, products: myFile},
    type: 'POST',
    success: function(data){
      alert(data);
    }
  });

   });
}

function addCategory() {
  var self = this;
  $('.addCategory').click(function(){

    var categoryName = $('.inputCatName').val();
    var CategoryExists = [];
    var nameCategory = false;
    
    for (var i = 0; i < ProductsArray.length; i++) {
      CategoryExists.push(ProductsArray[i].category); 
    }
    
    while(!nameCategory){
      for (var j = 0; j < CategoryExists.length; j++) {
        if (CategoryExists[j] === categoryName) {
          nameCategory=false;
          //  AddModal();
          break;
        }
        else {
          nameCategory=true;
        }
      }
    }

    var boxGroup = $('#boxOfGoodies');
   
    categoryName = capitalizeFirstLetter(categoryName);
    boxGroup.append('<div class="box"><div class="box-header"><div class="col-sm-5 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size"><h3>'+categoryName+'</h3></div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button"class="btn btn-block product-button addProduct">Adicionar Produtos</button> </div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button expandProducts">Lista de Produtos <span class="glyphicon glyphicon-menu-down"></span></button></div> <div class="col-sm-1 table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button category-button"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="inputFile" style="display:none"> <div ><label class="btn btn-primary col-sm-11 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size" for="my-file-selector"><input id="my-file-selector" name="file" type="file" style="display:none;"> <!--accept=".csv"--><h4>Escolher ficheiro</h4></label><button type="submit" form="form1" value="Submit" class="col-sm-1 table-col-border table-col-border-top contain-button col-size btn btn-danger addFile">Submeter</button></div></div><div class="box-body box-body-scroll"></div></div>');
      
    //Add new category to arrays
    Products.push(categoryName);
    Products.sort();

    var toInsert={
      "category": categoryName,
      "products":[]
    }
    ProductsArray.push(toInsert);

  });
}

function autoAddCategory(){
  $(document).ready(function(){
    ProductsArray.forEach(function(category){
      var self = this;
      var boxGroup = $('#boxOfGoodies');

      categoryName = capitalizeFirstLetter(category['category']);
      boxGroup.append('<div class="box"><div class="box-header"><div class="col-sm-5 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size"><h3>'+categoryName+'</h3></div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button"class="btn btn-block product-button addProduct">Adicionar Produtos</button> </div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button expandProducts">Lista de Produtos <span class="glyphicon glyphicon-menu-down"></span></button></div> <div class="col-sm-1 table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button category-button"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="inputFile" style="display:none"> <div ><label class="btn btn-primary col-sm-11 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size" for="my-file-selector"><input id="my-file-selector" name="file" type="file" style="display:none;"> <!--accept=".csv"--><h4>Escolher ficheiro</h4></label><button type="submit" form="form1" value="Submit" class="col-sm-1 table-col-border table-col-border-top contain-button col-size btn btn-danger addFile">Submeter</button></div></div><div class="box-body box-body-scroll"></div></div>');
      
      Products.push(categoryName);
      Products.sort();

      //Unbind all elements with the class and then rebbind to include the new element
      var expandCategoriaElement = boxGroup.find(".expandProducts");
      expandCategoriaElement.unbind("click", expand());
      expandCategoriaElement.bind("click", expand());
      var addProdutoElement = boxGroup.find(".addProduct");
      addProdutoElement.unbind("click", addProduct());
      addProdutoElement.bind("click", addProduct());
      var addFileElement = boxGroup.find(".btn-danger");
      addFileElement.unbind("click", addFile());
      addFileElement.bind("click", addFile());
      var removeProductElement = boxGroup.find(".box-of-products");
      removeProductElement.unbind("click", removeProduct());
      removeProductElement.bind("click", removeProduct()); 
      var removeCategoryElement = boxGroup.find(".category-button");
      removeCategoryElement.unbind("click", removeCategory());
      removeCategoryElement.bind("click", removeCategory()); 
      $(document).unbind("ready");
      $(document).bind("ready", function () { $("#my-file-selector").change(function(){var FileName = $(this).val().split('\\').pop(); $(this).parent().find('h4').text(FileName);}); });

      var lastChild = boxGroup.find(":last-child");
      //console.log("OLA " + lastChild.attr('class') );
      autoAddProducts(categoryName, lastChild);
    });
  });
}

function autoAddProducts(categoryName,lastChild) {
  var self = this;
  var boxProducts = lastChild.find(".box-body");
  for (var i = 0; i < ProductsArray.length; i++) {
    //console.log("OLA2 " + ProductsArray[i].category ); 
    if(ProductsArray[i].category === categoryName){
      for(var j = 0; j < ProductsArray[i].products.length; j++) {
        //console.log("OLA3 " + ProductsArray[i].products[j].name ); 
        product=capitalizeFirstLetter(ProductsArray[i].products[j].name);
        boxProducts.append('<div class="box-of-products"><div class="col-sm-10 table-col-border table-col-border-left product-padding col-size"> <h3>'+product+'</h3></div> <div class="col-sm-1 table-col-border contain-button col-size"><button type="button" class="btn btn-block product-button"><span class="glyphicon glyphicon-remove"></span></button> </div> </div>');
      }
    }
  }
}

//display name file
$(document).ready(function(){
   $("#my-file-selector").change(function(){
      var FileName = $(this).val().split('\\').pop();
      $(this).parent().find('h4').text(FileName);
   });
});

function removeProduct() {
  var self = this;
  $('.box-of-products').click(function(){
       
    var nameCategory = $(this).parent().parent().find(":first-child").find(":first-child");
    var product = $(this).find('h3').text();
    console.log("Cate " + nameCategory.find('h3:first').html() + " PROD " + product);

      /*$.ajax({   
        url: '/api/removep/',
        data: {category: nameCategory, products: product},
        type: 'POST',
        success: function(data){
          alert(data);
        }
        });*/

      $(this).remove();
  });
}

function removeCategory() {
   var self = this;
  $('.category-button').click(function(){
     
     var categoryName = $(this).parent().parent().find('h3').text();

     /*$.ajax({
      url: 'api/removec/',
      data: categoryName,
      type: 'POST',
      success: function(data){
        alert(data);
      }
    });*/

     $(this).parent().parent().parent().remove();
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
      modal.style.display = "none";
  }

  // When the user clicks anywhere outside of the modal, close it
  window.onclick = function(event) {
      if (event.target == modal) {
          modal.style.display = "none";
      }
  }
}

//UTILS
function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

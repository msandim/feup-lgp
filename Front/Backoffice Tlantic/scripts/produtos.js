function filter(){

  var ViewModel = function() {
    var self = this;

      self.filter = ko.observable('');

      self.items = ko.observableArray(["Televisão", "Computador", "Micro-ondas", "Frigorífico", "Telemóvel", "Colunas", "Rádio",
        "Televisão", "Computador", "Frigorífico", "Telemóvel", "Colunas", "Rádio", "Televisão", "Computador", "Micro-ondas", "Frigorífico",
         "Telemóvel", "Colunas", "Rádio", "Televisão", "Computador", "Frigorífico", "Telemóvel", "Colunas", "Rádio"]);

      self.items.sort();

      self.filteredItems = ko.computed(function() {
        var filter = self.filter().toLowerCase();
        if(!filter) { return self.items(); }
        return self.items().filter(function(i) {
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
    var ProductName;
    while(!ProductName){
      ProductName = prompt("Nome do Produto:");
    }
  
    var boxParent = $( this ).parent().parent().parent();
    var boxProducts = boxParent.find(".box-body");
    boxProducts.append('<div class="box-of-products"><div class="col-sm-10 table-col-border table-col-border-left product-padding col-size"><h3>'+ProductName+'</h3></div><div class="col-sm-1 table-col-border contain-button col-size"><button type="button" class="btn btn-block product-button"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box answers-inside"></div></div>');

  });
}

$(document).ready(function() {
  //remove products
  $('.box-of-products').click(function() {
    
    $(this).remove();
  });

  //remove categoria
  $('.category-button').click(function() {
    
    $(this).parent().parent().parent().remove();
  });
});
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


function expandCategoria() {
  var self = this;
  $('.expandCategoria').click(function(){
    var boxParent = $( this ).parent().parent().parent().parent();
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

function expandQuestion() {
  var self = this;
  $('.expandQuestion').click(function(){
    var boxParent = $( this ).parent().parent().parent();
    var boxBody = boxParent.children(".answers-inside");
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

function expandAnswer() {
  var self = this;
  $('.expandAnswer').click(function(){
    var boxParent = $( this ).parent().parent().parent();
    var boxBody = boxParent.children(".characteristics-inside");
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

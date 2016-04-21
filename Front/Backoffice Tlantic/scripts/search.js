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
  $('#expand-one').click(function(){
      $('.content-one').slideToggle('slow');
      var spanClass = $("#expand-one span").attr("class");
      if(spanClass == "glyphicon glyphicon-menu-up"){
        $("#expand-one span").attr("class","glyphicon glyphicon-menu-down");
      }else{
        $("#expand-one span").attr("class","glyphicon glyphicon-menu-up");
      }
  });
}

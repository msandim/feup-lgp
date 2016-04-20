function filter(){

  var ViewModel = function() {
    var self = this;
      
      self.filter = ko.observable('');
      
      self.items = ko.observableArray(["Televisão", "Computador", "Micro-ondas", "Frigorífico", "Telemóvel", "Colunas", "Rádio",
        "Televisão", "Computador", "Frigorífico", "Telemóvel", "Colunas", "Rádio", "Televisão", "Computador"]);
      
      self.filteredItems = ko.computed(function() {
        var filter = self.filter();
        if (!filter) { return self.items(); }
        return self.items().filter(function(i) { return i.indexOf(filter) > -1; });
      });
    };

    ko.applyBindings(new ViewModel());

}

function expand() {
  $('#expand-one').click(function(){
      $('.content-one').slideToggle('slow');
  });
}
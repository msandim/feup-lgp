//GLOBALS

  var categorias = ["Televisão", "Computador", "Micro-ondas", "Frigorífico", "Telemóvel", "Colunas", "Rádio",
  "Televisão", "Computador", "Frigorífico", "Telemóvel", "Colunas", "Rádio", "Televisão", "Computador", "Micro-ondas", "Frigorífico",
   "Telemóvel", "Colunas", "Rádio", "Televisão", "Computador", "Frigorífico", "Telemóvel", "Colunas", "Rádio"];

   categorias.sort();
   catItems=ko.observableArray();


function filter(){

  var ViewModel = function() {
    var self = this;

      self.filter = ko.observable('');

      /*self.items = ko.observableArray(["Televisão", "Computador", "Micro-ondas", "Frigorífico", "Telemóvel", "Colunas", "Rádio",
        "Televisão", "Computador", "Frigorífico", "Telemóvel", "Colunas", "Rádio", "Televisão", "Computador", "Micro-ondas", "Frigorífico",
         "Telemóvel", "Colunas", "Rádio", "Televisão", "Computador", "Frigorífico", "Telemóvel", "Colunas", "Rádio"]);*()

      self.items.sort();*/

      self.filteredItems = ko.computed(function() {
        /*var filter = self.filter().toLowerCase();
        if(!filter) { return self.items(); }
        return self.items().filter(function(i) {
          return i.toLowerCase().indexOf(filter) > -1;
        });*/
        var filter = self.filter().toLowerCase();
        if(!filter) { return catItems(); }
        return catItems().filter(function(i) {
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

function addCategory(){
  var self = this;
  $('#addCategory').click(function(){
    var categoryName;
    while(!categoryName){
      categoryName = prompt("Nome da Categoria:");
    }
    var boxGroup = $('#boxOfGoodies');

    categoryName = capitalizeFirstLetter(categoryName);
    boxGroup.append('<div class="box"><div class="box-header"><div class="row overflow-rows"><div class="col-sm-5 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size"><h3>'+categoryName+'</h3></div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button addQuestion">Adicionar Pergunta</button></div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button expandCategoria">Lista de Perguntas/Repostas <span class=" glyphicon glyphicon-menu-down"></span></button></div><div class="col-sm-1 table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button"><span class="glyphicon glyphicon-remove"></span></button></div></div></div><div class="box-body box-body-scroll"></div></div>');
    catItems.push(categoryName);
    catItems.sort();

    //Unbind all elements with the class and then rebbind to include the new element
    var addQuestionElement = boxGroup.find(".addQuestion");
    addQuestionElement.unbind("click", addQuestion());
    addQuestionElement.bind("click", addQuestion());
    var expandCategoriaElement = boxGroup.find(".expandCategoria");
    expandCategoriaElement.unbind("click", expandCategoria());
    expandCategoriaElement.bind("click", expandCategoria());

  });
}

function addQuestion(){
  var self = this;
  $('.addQuestion').click(function(){
    var question;
    while(!question){
      question = prompt("Pergunta:");
    }
    var boxParent = $( this ).parent().parent().parent().parent();
    var boxQuestions = boxParent.find(".box-body");

    question=capitalizeFirstLetter(question);
    boxQuestions.append('<div class="box-of-questions"><div class="row configurar-row-margin"><div class="col-sm-7 table-col-border table-col-border-left col-size contain-button" ><button type="button" class="btn btn-block product-button categorias-inside-menu-button expandQuestion"><span class=" glyphicon glyphicon-menu-down"></span> P.: '+question+'</button></div><div class="col-sm-3 table-col-border col-size contain-button"><button type="button" class="btn btn-block product-button addAnswer">Adicionar resposta</button></div><div class="col-sm-1 table-col-border contain-button col-size "><button type="button" class="btn btn-block product-button"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box answers-inside"></div></div>');

    //Unbind all elements with the class and then rebbind to include the new element
    var addAnswerElement = $(":root").find(".addAnswer");
    addAnswerElement.unbind("click", addAnswer());
    addAnswerElement.bind("click", addAnswer());
    var expandQuestionElement = $(":root").find(".expandQuestion");
    expandQuestionElement.unbind("click", expandQuestion());
    expandQuestionElement.bind("click", expandQuestion());

  });
}

function addAnswer(){
  var self = this;
  $('.addAnswer').click(function(){
    var answer;
    while(!answer){
      answer = prompt("Resposta:");
    }
    var boxParent = $( this ).parent().parent().parent();
    var boxAnswers = boxParent.find('.answers-inside')

    answer=capitalizeFirstLetter(answer);
    boxAnswers.append('<div class="div-with-answer"><div class="row configurar-row-margin"><div class="col-sm-7 table-col-border table-col-border-left col-size contain-button answer-box"><button type="button" class="btn btn-block product-button categorias-inside-menu-button answer-box expandAnswer"><span class=" glyphicon glyphicon-menu-down"></span> R.: '+answer+'</button></div><div class="col-sm-3 table-col-border col-size contain-button answer-box"><button type="button" class="btn btn-block product-button answer-box addCharacteristic">Adicionar carateristica</button></div><div class="col-sm-1 table-col-border contain-button col-size contain-button answer-box"><button type="button" class="btn btn-block product-button answer-box"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box characteristics-inside"></div></div>');

    //Unbind all elements with the class and then rebbind to include the new element
    var addCharacteristicElement = $(":root").find(".addCharacteristic");
    addCharacteristicElement.unbind("click", addCharacteristic());
    addCharacteristicElement.bind("click", addCharacteristic());
    var expandAnswerElement = $(":root").find(".expandAnswer");
    console.log(expandAnswerElement.attr("id"));
    expandAnswerElement.unbind("click", expandAnswer());
    expandAnswerElement.bind("click", expandAnswer());
  });
}

function addCharacteristic(){
  var self = this;
  $('.addCharacteristic').click(function(){
    var score;
    var value;
    var operator;
    var name;
    while(!name){
      name = prompt("Nome:");
    }
    while(!score){
      score = prompt("Score:");
    }
    while(!value){
      value = prompt("Value:");
    }
    while(!operator){
      operator = prompt("Operator:");
    }
    var boxParent = $( this ).parent().parent().parent();
    var boxCharacteristic = boxParent.find('.characteristics-inside');
    boxCharacteristic.append('<div class="div-with-characteristics"><div class="row configurar-row-margin"><div class="col-sm-2 table-col-border table-col-border-left col-size"><h4>Caraterística</h4></div><div class="col-sm-2 table-col-border col-size"><h4>Score</h4></div><div class="col-sm-2 table-col-border col-size"><h4>Valor</h4></div><div class="col-sm-2 table-col-border col-size"><h4>Operador</h4></div><div class="col-sm-2 table-col-border col-size"><h4>Nome</h4></div><div class="col-sm-1 table-col-border contain-button col-size"><button type="button" class="btn btn-block product-button"><span class="glyphicon glyphicon-remove"></span></button></div><div class="col-sm-2 col-size"></div><div class="col-sm-2 table-col-border-right table-col-border-left col-size input-padding"><input type="text" class="form-control" placeholder="'+score+'"></div><div class="col-sm-2 table-col-border col-size input-padding"><input type="text" class="form-control" placeholder="'+value+'"></div><div class="col-sm-2 table-col-border col-size input-padding"><input type="text" class="form-control" placeholder="'+operator+'"></div><div class="col-sm-2 table-col-border col-size input-padding"><input type="text" class="form-control" placeholder="'+name+'"></div></div></div>');

  });
}

function autoAddCategory(){
  $(document).ready(function(){
    categorias.forEach(function(categoryName) {
      var self = this;
      var boxGroup = $('#boxOfGoodies');

      categoryName = capitalizeFirstLetter(categoryName);
      boxGroup.append('<div class="box"><div class="box-header"><div class="row overflow-rows"><div class="col-sm-5 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size"><h3>'+categoryName+'</h3></div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button addQuestion">Adicionar Pergunta</button></div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button expandCategoria">Lista de Perguntas/Repostas <span class=" glyphicon glyphicon-menu-down"></span></button></div><div class="col-sm-1 table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button"><span class="glyphicon glyphicon-remove"></span></button></div></div></div><div class="box-body box-body-scroll"></div></div>');
      catItems.push(categoryName);
      catItems.sort();

      //Unbind all elements with the class and then rebbind to include the new element
      var addQuestionElement = boxGroup.find(".addQuestion");
      addQuestionElement.unbind("click", addQuestion());
      addQuestionElement.bind("click", addQuestion());
      var expandCategoriaElement = boxGroup.find(".expandCategoria");
      expandCategoriaElement.unbind("click", expandCategoria());
      expandCategoriaElement.bind("click", expandCategoria());
    });
  });
}


//UTILS

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

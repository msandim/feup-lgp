//GLOBALS

//server:"http://intelligentsalesguide.me/",
//  getAllCategories:"api/allCategories",
// getQuestionsByCategory  -> CODE Código da categoria.  => GET
// api/sequencesByCategory -> CODE Código da categoria. => GET
// api/addQuestions/ “category”: categoria onde adicionar as perguntas./“questions”: Perguntas a serem adicionadas.  => POST
// api/removeQuestions/  “questions”: Código das Perguntas a serem removidas.


   //Trocar por serviço
   var categoryArray =
     [
       {
         "category":"Televisão",
          "questions":[
            {
              "text":"Qual o tamanho da sua sala?",
              "answers":[
                {
                  "text":"Pequena",
                  "caracteristics":[
                    {
                      "name":"width (cm)",
                      "operator":"<",
                      "value":"400",
                      "score":"0.5"
                    },
                    {
                      "name":"resolution",
                      "operator":"=",
                      "value":"720p",
                      "score":"0.1"
                    }
                  ]
                },
                {
                  "text":"Média",
                  "caracteristics":[
                    {
                      "name":"width (cm)",
                      "operator":">",
                      "value":"200",
                      "score":"0.5"
                    },
                    {
                      "name":"resolution",
                      "operator":"=",
                      "value":"1080p",
                      "score":"0.1"
                    }
                  ]
                },
                {
                  "text":"Grande",
                  "caracteristics":[
                    {
                      "name":"width (cm)",
                      "operator":"<",
                      "value":"100",
                      "score":"0.5"
                    },
                    {
                      "name":"resolution",
                      "operator":"=",
                      "value":"2K",
                      "score":"0.1"
                    }
                  ]
                }
              ]
            }
          ]
        }
      ];

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
    var boxParent = $( this ).closest(".box");
    var boxBody = boxParent.children(".box-of-questions");
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
    var boxParent = $( this ).closest(".box");
    var boxBody = boxParent.children(".box-of-answers");
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
    var boxParent = $( this ).closest('.box');
    var boxBody = boxParent.children(".box-of-characteristics");
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
  $('.AddCategory').click(function(){
    
    var categoryName = $('.inputCatName').val();
    var CategoryExists = [];
    var nameCategory = false;

    for (var i = 0; i < categoryArray.length; i++) {
      CategoryExists.push(categoryArray[i].category); 
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
    }

    var boxGroup = $('#boxOfGoodies');

    categoryName = capitalizeFirstLetter(categoryName);
    boxGroup.append('<div class="box"><div class="row"><div class="col-xs-4 table-col-border-first table-col-size table-contain-h3"><h3>'+categoryName+'</h3></div><div class="col-xs-3 table-col-border-first table-col-size table-contain-button"><button type="button" href="#" class="btn btn-block table-button toggle-login inputQuestion">Adicionar Pergunta</button><div style="display:none" class="login"><div id="triangle"></div><h1>Nova Pergunta</h1><input type="text" class="value" placeholder=">" required/><input class="addQuestion" type="submit" value="Adicionar" /></div></div><div class="col-xs-4 table-col-border-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandCategoria">Lista de Perguntas/Repostas <span class=" glyphicon glyphicon-menu-down"></span></button></div><div class="col-xs-1 table-col-border-end-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeCategory"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box-of-questions"><div class="container-fluid"></div></div></div>');
        
    //Add new category to arrays
    catItems.push(categoryName);
    catItems.sort();

    var toInsert={
      "category": categoryName,
      "questions":[]
    }
    categoryArray.push(toInsert);

    //Unbind all elements with the class and then rebbind to include the new element
    var addQuestionElement = boxGroup.find(".addQuestion");
    addQuestionElement.unbind("click", addQuestion());
    addQuestionElement.bind("click", addQuestion());
    var expandCategoriaElement = boxGroup.find(".expandCategoria");
    expandCategoriaElement.unbind("click", expandCategoria());
    expandCategoriaElement.bind("click", expandCategoria());
    var removeCategoryElement = boxGroup.find(".removeCategory");
    removeCategoryElement.unbind("click", removeCategory());
    removeCategoryElement.bind("click", removeCategory());
    $(document).unbind("ready");
    $(document).bind("ready", function () {  $(document).on('click','.addQuestion',function(){ var parent = $( this ).parent(); var question = parent.children("input[type='text']").val(); if(question != "") {$('.login').val(''); $('.login').attr("placeholder", ">"); $(".login").click(); } }); $("document ").on("click", ".toggle-login", function(){$(this).next(".login").toggle(); });  $(document).on('click','.addCharacteristic',function(){ var parent = $( this ).parent(); var operator = parent.children("input[id='inputOperator']").val(); var value = parent.children("input[id='inputValue']").val();var score = parent.children("input[id='inputScore']").val(); var characteristic = parent.children("input[id='inputCharacteristic']").val(); if(operator != "" && value != "" && score != "" && characteristic != "") { $('.login').val(''); $('.login').attr("placeholder", ">"); $('.login').hide(); }});}); 

    //clear placeholder
    $('.inputCatName').val('');
    $('.inputCatName').attr("placeholder", ">");

    //disable button submit add category
    $('#InputButtonCatName').attr("disabled", true);
  }); 
}

function addQuestion(){
  var self = this;
  $('.addQuestion').click(function(){
    
    var parent = $( this ).parent();
    var question = parent.children("input[type='text']").val();

     if(question == "") {
      //edit placeholder
      parent.children("input[type='text']").val('');
      parent.children("input[type='text']").attr("placeholder", "> Insira algo.");
      return;
    }

    var boxParent = $( this ).closest('.box');
    var boxQuestions = boxParent.find(".box-of-questions").find(">:first-child");

    question=capitalizeFirstLetter(question);
    boxQuestions.append('<div class="box"><div class="row"><div class="col-xs-7 table-col-border table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandQuestion"><span class=" glyphicon glyphicon-menu-down"></span>P.: '+question+'</button></div><div class="col-xs-3 table-col-border table-col-size table-contain-button"><button type="button" href="#" class="btn btn-block table-button toggle-login inputQuestion">Adicionar Resposta</button><div style="display:none" class="login"><div id="triangle"></div><h1>Adicionar Resposta</h1><input type="text" class="value" placeholder=">" required/><input class="addAnswer" type="submit" value="Adicionar" /></div></div><div class="col-xs-1 table-col-border-end table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeQuestion"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box-of-answers"><div class="container-fluid"></div></div></div>');
    
    //Add question to array
    var categoryText= $(this).parent().parent().find('h3').text();

    var toInsert = {
      "text": question,
      "answers":[]
    }

    categoryArray.forEach(function(category){
      if(category['category']==categoryText){
        category['questions'].push(toInsert);
      }
    });

    //Unbind all elements with the class and then rebbind to include the new element
    var addAnswerElement = $(":root").find(".addAnswer");
    addAnswerElement.unbind("click", addAnswer());
    addAnswerElement.bind("click", addAnswer());
    var expandQuestionElement = $(":root").find(".expandQuestion");
    expandQuestionElement.unbind("click", expandQuestion());
    expandQuestionElement.bind("click", expandQuestion());
    var removeQuestionElement = boxQuestions.find(".removeQuestion");
    removeQuestionElement.unbind("click", removeQuestion());
    removeQuestionElement.bind("click", removeQuestion());
    
    //clear placeholder
    parent.hide();
    parent.children("input[type='text']").val('');
    parent.children("input[type='text']").attr("placeholder", ">");
  });
}

function addAnswer(){
  var self = this;
  $('.addAnswer').click(function(){
   
    var parent = $( this ).parent();
    var answer = parent.children("input[type='text']").val();

    if(answer == "") {
      //edit placeholder
      parent.children("input[type='text']").val('');
      parent.children("input[type='text']").attr("placeholder", "> Insira algo.");
      return;
    }

    var boxParent = $( this ).closest('.box');
    var boxAnswers = boxParent.find('.box-of-answers').find('>:first-child');

    answer=capitalizeFirstLetter(answer);
    boxAnswers.append('<div class="box"><div class="row"><div class="col-xs-7 table-col-border table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandAnswer"><span class=" glyphicon glyphicon-menu-down"></span>R.: '+answer+'</button></div><div class="col-xs-3 table-col-border table-col-size table-contain-button"><button type="button" href="#" class="btn btn-block table-button toggle-login">Adicionar Carateristica</button> <div style="display:none" class="login"><div id="triangle"></div><h1>Adicionar Resposta</h1><input type="text" id="inputCharacteristic" class="value" placeholder="> Caracteristica" required/> <input type="text" id="inputOperator" class="value" placeholder="> Operador" required/><input type="text" id="inputValue" class="value" placeholder="> Valor" required/><input type="text" id="inputScore" class="value" placeholder="> Score" required/><input class="addCharacteristic" type="submit" value="Adicionar" /></div></div><div class="col-xs-1 table-col-border-end table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeAnswer"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box-of-characteristics"><div class="container-fluid"></div></div></div>');

    //Add answer to array
    var categoryText= $(this).closest('.box').find('h3').text();
    var questionText = $(this).parent().parent().find('.expandQuestion').text();
    questionText = questionText.substr(5,questionText.length);

    var toInsert = {
      "text": answer,
      "caracteristics":[]
    }

    categoryArray.forEach(function(category){
      if(category['category']==categoryText){
        category['questions'].forEach(function(question){
          if(question['text']==questionText){
            question['answers'].push(toInsert);
          }
        });
      }
    });

    //Unbind all elements with the class and then rebbind to include the new element
    var addCharacteristicElement = $(":root").find(".addCharacteristic");
    addCharacteristicElement.unbind("click", addCharacteristic());
    addCharacteristicElement.bind("click", addCharacteristic());
    var expandAnswerElement = $(":root").find(".expandAnswer");
    expandAnswerElement.unbind("click", expandAnswer());
    expandAnswerElement.bind("click", expandAnswer());
    var removeAnswerElement = boxAnswers.find(".removeAnswer");
    removeAnswerElement.unbind("click", removeAnswer());
    removeAnswerElement.bind("click", removeAnswer());

    //clear placeholder
    parent.hide();
    parent.children("input[type='text']").val('');
    parent.children("input[type='text']").attr("placeholder", ">");
  });
}

function addCharacteristic(){
  var self = this;
  $('.addCharacteristic').click(function(){

    var parent = $( this ).parent();
    var score = parent.children("input[id='inputScore']").val();
    var value = parent.children("input[id='inputValue']").val();
    var operator = parent.children("input[id='inputOperator']").val();
    var name = parent.children("input[id='inputCharacteristic']").val();

    if(score == "" || value == "" || operator == "" || name == "" ) {
        if(score == "") {
            //edit placeholder
          parent.children("input[id='inputScore']").val('');
          parent.children("input[id='inputScore']").attr("placeholder", "> Insira algo.");
        }
        if ( value == "") {
           //edit placeholder
          parent.children("input[id='inputValue']").val('');
          parent.children("input[id='inputValue']").attr("placeholder", "> Insira algo.");
        }
        if ( operator == "") {
           //edit placeholder
          parent.children("input[id='inputOperator']").val('');
          parent.children("input[id='inputOperator']").attr("placeholder", "> Insira algo.");
        }
        if ( name == "" ) {
          //edit placeholder
          parent.children("input[id='inputCharacteristic']").val('');
          parent.children("input[id='inputCharacteristic']").attr("placeholder", "> Insira algo.");
        }
      return;
    }
 
    var boxParent = $( this ).closest('.box');
    //console.log(boxParent.attr("class"));
    var boxCharacteristic = boxParent.find('.box-of-characteristics').find('>:first-child');
    boxCharacteristic.append('<div class="box"><div class="row"><div class="col-xs-2 table-col-border table-col-size"><h4>Caraterística</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4>Operador</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4>Valor</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4>Score</h4></div><div class="col-xs-1 table-col-border table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeCharacteristic"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="row"><div class="col-xs-2 table-col-border table-col-size input-padding"><input type="text" class="form-control" placeholder="'+name+'"></div><div class="col-xs-2 table-col-border table-col-size input-padding"><input type="text" class="form-control" placeholder="'+operator+'"></div><div class="col-sm-2 table-col-border table-col-size input-padding"><input type="text" class="form-control" placeholder="'+value+'"></div><div class="col-sm-2 table-col-border table-col-size input-padding"><input type="text" class="form-control" placeholder="'+score+'"></div></div></div>');

    //Add characteristic to array
    var categoryText = $(this).closest('.box-of-questions').parent().parent().find('h3').text();
    var questionText = $(this).closest('.box-of-questions').find('.expandQuestion').text();
    questionText = questionText.substr(5,questionText.length);
    var answerText = $(this).closest('.div-with-answer').find('.expandAnswer').text();
    answerText = answerText.substr(5,answerText.length);

    var toInsert = {
      "name":name,
      "operator":operator,
      "value":value,
      "score":score
    }

    categoryArray.forEach(function(category){
      if(category['category']==categoryText){
        category['questions'].forEach(function(question){
          if(question['text']==questionText){
            question['answers'].forEach(function(answer){
              if(answer['text']==answerText){
                answer['caracteristics'].push(toInsert);
              }
            });
          }
        });
      }
    });

    var removeCharacteristicElement = boxCharacteristic.find(".removeCharacteristic");
    removeCharacteristicElement.unbind("click", removeCharacteristic());
    removeCharacteristicElement.bind("click", removeCharacteristic());

     parent.hide();
     //clear placeholder
    parent.children("input[id='inputScore']").val('');
    parent.children("input[id='inputValue']").val('');
    parent.children("input[id='inputOperator']").val('');
    parent.children("input[id='inputCharacteristic']").val('');
    parent.children("input[id='inputScore']").attr("placeholder", "> Caracteristica");
    parent.children("input[id='inputValue']").attr("placeholder", "> Valor");
    parent.children("input[id='inputOperator']").attr("placeholder", "> Operador");
    parent.children("input[id='inputCharacteristic']").attr("placeholder", "> Score");
  });
}


function autoAddCategory(){
  $(document).ready(function(){
    categoryArray.forEach(function(category){
      var self = this;
      var boxGroup = $('#boxOfGoodies');

      categoryName = capitalizeFirstLetter(category['category']);
       boxGroup.append('<div class="box"><div class="row"><div class="col-xs-4 table-col-border-first table-col-size table-contain-h3"><h3>'+categoryName+'</h3></div><div class="col-xs-3 table-col-border-first table-col-size table-contain-button"><button type="button" href="#" class="btn btn-block table-button toggle-login inputQuestion">Adicionar Pergunta</button><div style="display:none" class="login"><div id="triangle"></div><h1>Nova Pergunta</h1><input type="text" class="value" placeholder=">" required/><input class="addQuestion" type="submit" value="Adicionar" /></div></div><div class="col-xs-4 table-col-border-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandCategoria">Lista de Perguntas/Repostas <span class=" glyphicon glyphicon-menu-down"></span></button></div><div class="col-xs-1 table-col-border-end-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeCategory"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box-of-questions"><div class="container-fluid"></div></div></div>');
      
      catItems.push(categoryName);
      catItems.sort();

      //Unbind all elements with the class and then rebbind to include the new element
      var addQuestionElement = boxGroup.find(".addQuestion");
      addQuestionElement.unbind("click", addQuestion());
      addQuestionElement.bind("click", addQuestion());
      var expandCategoriaElement = boxGroup.find(".expandCategoria");
      expandCategoriaElement.unbind("click", expandCategoria());
      expandCategoriaElement.bind("click", expandCategoria());
      var removeCategoryElement = boxGroup.find(".removeCategory");
      removeCategoryElement.unbind("click", removeCategory());
      removeCategoryElement.bind("click", removeCategory());
      $(document).unbind("ready");
       $(document).bind("ready", function () {  $(document).on('click','.addQuestion',function(){ var parent = $( this ).parent(); var question = parent.children("input[type='text']").val(); if(question != "") {$('.login').val(''); $('.login').attr("placeholder", ">"); $(".login").click(); } }); $("document ").on("click", ".toggle-login", function(){$(this).next(".login").toggle(); });  $(document).on('click','.addCharacteristic',function(){ var parent = $( this ).parent(); var operator = parent.children("input[id='inputOperator']").val(); var value = parent.children("input[id='inputValue']").val();var score = parent.children("input[id='inputScore']").val(); var characteristic = parent.children("input[id='inputCharacteristic']").val(); if(operator != "" && value != "" && score != "" && characteristic != "") { $('.login').val(''); $('.login').attr("placeholder", ">"); $('.login').hide(); }});}); 

      var lastChild = boxGroup.find(">:last-child");

      autoAddQuestions(categoryName,lastChild);
    });
  });
}


function autoAddQuestions(categoryName,lastChild){
  var self = this;

  categoryArray.forEach(function(questionFor){
    var boxQuestions = lastChild.find('.box-of-questions').find(">:first-child");

    if(questionFor['category']===categoryName){
      questionFor['questions'].forEach(function(quest){
        question=capitalizeFirstLetter(quest['text']);
         boxQuestions.append('<div class="box"><div class="row"><div class="col-xs-7 table-col-border table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandQuestion"><span class=" glyphicon glyphicon-menu-down"></span>P.: '+question+'</button></div><div class="col-xs-3 table-col-border table-col-size table-contain-button"><button type="button" href="#" class="btn btn-block table-button toggle-login inputQuestion">Adicionar Resposta</button><div style="display:none" class="login"><div id="triangle"></div><h1>Adicionar Resposta</h1><input type="text" class="value" placeholder=">" required/><input class="addAnswer" type="submit" value="Adicionar" /></div></div><div class="col-xs-1 table-col-border-end table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeQuestion"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box-of-answers"><div class="container-fluid"></div></div></div>');
    
        //Unbind all elements with the class and then rebbind to include the new element
        var addAnswerElement = $(":root").find(".addAnswer");
        addAnswerElement.unbind("click", addAnswer());
        addAnswerElement.bind("click", addAnswer());
        var expandQuestionElement = $(":root").find(".expandQuestion");
        expandQuestionElement.unbind("click", expandQuestion());
        expandQuestionElement.bind("click", expandQuestion());
        var removeQuestionElement = boxQuestions.find(".removeQuestion");
        removeQuestionElement.unbind("click", removeQuestion());
        removeQuestionElement.bind("click", removeQuestion());
       

        var newLastChild = boxQuestions.find(":last-child");
        autoAddAnswers(quest['answers'],newLastChild);
      });
    }
  });
}


function autoAddAnswers(answers,lastChild){
  var self = this;

  answers.forEach(function(singleAnswer){

    var boxAnswers = lastChild.find(".box-of-answers").find(">:first-child");

    answer = capitalizeFirstLetter(singleAnswer['text']);
    boxAnswers.append('<div class="box"><div class="row"><div class="col-xs-7 table-col-border table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandAnswer"><span class=" glyphicon glyphicon-menu-down"></span>R.: '+answer+'</button></div><div class="col-xs-3 table-col-border table-col-size table-contain-button"><button type="button" href="#" class="btn btn-block table-button toggle-login">Adicionar Carateristica</button> <div style="display:none" class="login"><div id="triangle"></div><h1>Adicionar Resposta</h1><input type="text" id="inputCharacteristic" class="value" placeholder="> Caracteristica" required/> <input type="text" id="inputOperator" class="value" placeholder="> Operador" required/><input type="text" id="inputValue" class="value" placeholder="> Valor" required/><input type="text" id="inputScore" class="value" placeholder="> Score" required/><input class="addCharacteristic" type="submit" value="Adicionar" /></div></div><div class="col-xs-1 table-col-border-end table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeAnswer"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box-of-characteristics"><div class="container-fluid"></div></div></div>');

    //Unbind all elements with the class and then rebbind to include the new element
    var addCharacteristicElement = $(":root").find(".addCharacteristic");
    addCharacteristicElement.unbind("click", addCharacteristic());
    addCharacteristicElement.bind("click", addCharacteristic());
    var expandAnswerElement = $(":root").find(".expandAnswer");
    expandAnswerElement.unbind("click", expandAnswer());
    expandAnswerElement.bind("click", expandAnswer());
    var removeAnswerElement = boxAnswers.find(".removeAnswer");
    removeAnswerElement.unbind("click", removeAnswer());
    removeAnswerElement.bind("click", removeAnswer());

    var newLastChild = boxAnswers.find(":last-child");
    autoAddCharacteristics(singleAnswer['caracteristics'],newLastChild);

  });
}

function autoAddCharacteristics(characteristics,lastChild){
  var self = this;

  characteristics.forEach(function(singleCharacteristic){

    var boxCharacteristic = lastChild.find(".box-of-characteristics").find(">:first-child");

    boxCharacteristic.append('<div class="box"><div class="row"><div class="col-xs-2 table-col-border table-col-size"><h4>Caraterística</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4>Operador</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4>Valor</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4>Score</h4></div><div class="col-xs-1 table-col-border table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeCharacteristic"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="row"><div class="col-xs-2 table-col-border table-col-size input-padding"><input type="text" class="form-control" placeholder="'+singleCharacteristic['name']+'"></div><div class="col-xs-2 table-col-border table-col-size input-padding"><input type="text" class="form-control" placeholder="'+singleCharacteristic['operator']+'"></div><div class="col-sm-2 table-col-border table-col-size input-padding"><input type="text" class="form-control" placeholder="'+singleCharacteristic['value']+'"></div><div class="col-sm-2 table-col-border table-col-size input-padding"><input type="text" class="form-control" placeholder="'+singleCharacteristic['score']+'"></div></div></div>');

    var removeCharacteristicElement = boxCharacteristic.find(".removeCharacteristic");
    removeCharacteristicElement.unbind("click", removeCharacteristic());
    removeCharacteristicElement.bind("click", removeCharacteristic());

  });
}

function removeCategory() {
  var self = this;
  $('.removeCategory').click(function(){

    var catElement = $(this).parent().parent().find("h3");
    var catName = catElement.text();
    for(var i=0; i<categoryArray.length; i++){
      if(catName==categoryArray[i]['category']){
        categoryArray.splice(i,1);
        var aux = catItems.indexOf(catName);
        catItems.splice(aux,1);
        break;
      }
    }

    $(this).closest('.box').remove();
  });
}

function removeQuestion() {
  var self = this;
  $('.removeQuestion').click(function(){
    var categoryText = $(this).parent().parent().parent().parent().parent().find('h3').text();
    var questionText = $(this).parent().parent().find('.expandQuestion').text();
    questionText = questionText.substr(5,questionText.length);
    categoryArray.forEach(function(category){
      if(category['category']==categoryText){
        for(var i=0; i<category['questions'].length; i++){
          if(category['questions'][i]['text'] == questionText){
            category['questions'].splice(i,1);
          }
        }
      }
    });

    $(this).parent().parent().parent().remove();


  });
}

function removeAnswer() {
  var self = this;
  $('.removeAnswer').click(function(){
    var categoryText = $(this).closest('.box-of-questions').parent().parent().find('h3').text();
    var questionText = $(this).closest('.box-of-questions').find('.expandQuestion').text();
    questionText = questionText.substr(5,questionText.length);
    var answerText = $(this).closest('.div-with-answer').find('.expandAnswer').text();
    answerText = answerText.substr(5,answerText.length);
    categoryArray.forEach(function(category){
      if(category['category']==categoryText){
        category['questions'].forEach(function(question){
          for(var i=0; i<question['answers'].length; i++){
            if(question['answers'][i]['text'] == answerText){
              question['answers'].splice(i,1);
            }
          }
        });
      }
    });

    $(this).parent().parent().parent().remove();
  });
}

function removeCharacteristic() {
  var self = this;
  $('.removeCharacteristic').click(function(){

    var categoryText = $(this).closest('.box-of-questions').parent().parent().find('h3').text();
    var questionText = $(this).closest('.box-of-questions').find('.expandQuestion').text();
    questionText = questionText.substr(5,questionText.length);
    var answerText = $(this).closest('.div-with-answer').find('.expandAnswer').text();
    answerText = answerText.substr(5,answerText.length);
    var characteristicText = $(this).closest('.configurar-row-margin').find('.charName').attr("placeholder");
    categoryArray.forEach(function(category){
      if(category['category']==categoryText){
        category['questions'].forEach(function(question){
          if(question['text']==questionText){
            question['answers'].forEach(function(answer){
              if(answer['text']==answerText){
                for(var i=0; i<answer['caracteristics'].length; i++){
                  if(answer['caracteristics'][i]['name'] == characteristicText){
                    console.log(answer['caracteristics'][i]['name']);
                    answer['caracteristics'].splice(i,1);
                    console.log(answer['caracteristics'][i]['name']);
                  }
                }
              }
            });
          }
        });
      }
    });

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

  $('.AddCategory').click(function(){
      modal.style.display = "none";
  });
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

    //check empty fields
    /*$(document).on('click','.addQuestion',function(){
      var parent = $( this ).parent();
      var question = parent.children("input[type='text']").val();
      if(question == ""){
        //edit value placeholder
        parent.children("input[type='text']").val('');
        parent.children("input[type='text']").attr("placeholder", "> Insira algo"); 
        }
      });*/
        
});
  
//
$(document).ready(function() {

   $(document).on('click','.addQuestion',function(){
      
    var parent = $( this ).parent();
    var question = parent.children("input[type='text']").val();

    if(question != "") {
      //edit value placeholder
      $('.login').val('');
      $('.login').attr("placeholder", ">");
      $(".login").click();
    }
  });

  $(document).on('click','.addAnswer',function(){

    var parent = $( this ).parent();
    var answer = parent.children("input[type='text']").val();

    if(answer != "") {
      //edit value placeholder
      $('.login').val('');
      $('.login').attr("placeholder", ">");

      $('.login').hide();
    }
  });

  $(document).on('click','.addCharacteristic',function(){

    var parent = $( this ).parent();
    var operator = parent.children("input[id='inputOperator']").val();
    var value = parent.children("input[id='inputValue']").val();
    var score = parent.children("input[id='inputScore']").val();
    var characteristic = parent.children("input[id='inputCharacteristic']").val();

    if(operator != "" && value != "" && score != "" && characteristic != "") {
        //edit value placeholder
      $('.login').val('');
      $('.login').attr("placeholder", ">");

      $('.login').hide();
    }
  });

  $(document).on('click','.toggle-login',function(){
      $(this).next(".login").toggle();
  });

});


//UTILS

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

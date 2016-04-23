//GLOBALS

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
    boxGroup.append('<div class="box"><div class="box-header"><div class="row overflow-rows"><div class="col-sm-5 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size"><h3>'+categoryName+'</h3></div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button addQuestion">Adicionar Pergunta</button></div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button expandCategoria">Lista de Perguntas/Repostas <span class=" glyphicon glyphicon-menu-down"></span></button></div><div class="col-sm-1 table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button removeCategory"><span class="glyphicon glyphicon-remove"></span></button></div></div></div><div class="box-body box-body-scroll"></div></div>');

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
    boxQuestions.append('<div class="box-of-questions"><div class="row configurar-row-margin"><div class="col-sm-7 table-col-border table-col-border-left col-size contain-button" ><button type="button" class="btn btn-block product-button categorias-inside-menu-button expandQuestion"><span class=" glyphicon glyphicon-menu-down"></span> P.: '+question+'</button></div><div class="col-sm-3 table-col-border col-size contain-button"><button type="button" class="btn btn-block product-button addAnswer">Adicionar resposta</button></div><div class="col-sm-1 table-col-border contain-button col-size "><button type="button" class="btn btn-block product-button removeQuestion"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box answers-inside"></div></div>');

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
    boxAnswers.append('<div class="div-with-answer"><div class="row configurar-row-margin"><div class="col-sm-7 table-col-border table-col-border-left col-size contain-button answer-box"><button type="button" class="btn btn-block product-button categorias-inside-menu-button answer-box expandAnswer"><span class=" glyphicon glyphicon-menu-down"></span> R.: '+answer+'</button></div><div class="col-sm-3 table-col-border col-size contain-button answer-box"><button type="button" class="btn btn-block product-button answer-box addCharacteristic">Adicionar carateristica</button></div><div class="col-sm-1 table-col-border contain-button col-size contain-button answer-box"><button type="button" class="btn btn-block product-button answer-box removeAnswer"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box characteristics-inside"></div></div>');

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
    boxCharacteristic.append('<div class="div-with-characteristics"><div class="row configurar-row-margin"><div class="col-sm-2 table-col-border table-col-border-left col-size"><h4>Caraterística</h4></div><div class="col-sm-2 table-col-border col-size"><h4>Score</h4></div><div class="col-sm-2 table-col-border col-size"><h4>Valor</h4></div><div class="col-sm-2 table-col-border col-size"><h4>Operador</h4></div><div class="col-sm-2 table-col-border col-size"><h4>Nome</h4></div><div class="col-sm-1 table-col-border contain-button col-size"><button type="button" class="btn btn-block product-button removeCharacteristic"><span class="glyphicon glyphicon-remove"></span></button></div><div class="col-sm-2 col-size"></div><div class="col-sm-2 table-col-border-right table-col-border-left col-size input-padding"><input type="text" class="form-control" placeholder="'+score+'"></div><div class="col-sm-2 table-col-border col-size input-padding"><input type="text" class="form-control" placeholder="'+value+'"></div><div class="col-sm-2 table-col-border col-size input-padding"><input type="text" class="form-control" placeholder="'+operator+'"></div><div class="col-sm-2 table-col-border col-size input-padding"><input type="text" class="form-control charName" placeholder="'+name+'"></div></div></div>');

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

  });
}

function autoAddCategory(){
  $(document).ready(function(){
    categoryArray.forEach(function(category){
      var self = this;
      var boxGroup = $('#boxOfGoodies');

      categoryName = capitalizeFirstLetter(category['category']);
      boxGroup.append('<div class="box"><div class="box-header"><div class="row overflow-rows"><div class="col-sm-5 product-padding table-col-border table-col-border-left table-col-border-top color-backgorund-category col-size"><h3>'+categoryName+'</h3></div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button addQuestion">Adicionar Pergunta</button></div><div class="col-sm-3 text-center table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button expandCategoria">Lista de Perguntas/Repostas <span class=" glyphicon glyphicon-menu-down"></span></button></div><div class="col-sm-1 table-col-border table-col-border-top contain-button col-size"><button type="button" class="btn btn-block product-button removeCategory"><span class="glyphicon glyphicon-remove"></span></button></div></div></div><div class="box-body box-body-scroll"></div></div>');
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

      var lastChild = boxGroup.find(":last-child");

      autoAddQuestions(categoryName,lastChild);
    });
  });
}


function autoAddQuestions(categoryName,lastChild){
  var self = this;

  categoryArray.forEach(function(questionFor){

    var boxQuestions = lastChild.find(".box-body");

    if(questionFor['category']===categoryName){
      questionFor['questions'].forEach(function(quest){
        question=capitalizeFirstLetter(quest['text']);
        boxQuestions.append('<div class="box-of-questions"><div class="row configurar-row-margin"><div class="col-sm-7 table-col-border table-col-border-left col-size contain-button" ><button type="button" class="btn btn-block product-button categorias-inside-menu-button expandQuestion"><span class=" glyphicon glyphicon-menu-down"></span> P.: '+question+'</button></div><div class="col-sm-3 table-col-border col-size contain-button"><button type="button" class="btn btn-block product-button addAnswer">Adicionar resposta</button></div><div class="col-sm-1 table-col-border contain-button col-size "><button type="button" class="btn btn-block product-button removeQuestion"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box answers-inside"></div></div>');

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

    var boxAnswers = lastChild.find(".answers-inside");

    answer = capitalizeFirstLetter(singleAnswer['text']);
    boxAnswers.append('<div class="div-with-answer"><div class="row configurar-row-margin"><div class="col-sm-7 table-col-border table-col-border-left col-size contain-button answer-box"><button type="button" class="btn btn-block product-button categorias-inside-menu-button answer-box expandAnswer"><span class=" glyphicon glyphicon-menu-down"></span> R.: '+answer+'</button></div><div class="col-sm-3 table-col-border col-size contain-button answer-box"><button type="button" class="btn btn-block product-button answer-box addCharacteristic">Adicionar carateristica</button></div><div class="col-sm-1 table-col-border contain-button col-size contain-button answer-box"><button type="button" class="btn btn-block product-button answer-box removeAnswer"><span class="glyphicon glyphicon-remove"></span></button></div></div><div class="box characteristics-inside"></div></div>');

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

    var boxCharacteristic = lastChild.find(".characteristics-inside");

    boxCharacteristic.append('<div class="div-with-characteristics"><div class="row configurar-row-margin"><div class="col-sm-2 table-col-border table-col-border-left col-size"><h4>Caraterística</h4></div><div class="col-sm-2 table-col-border col-size"><h4>Score</h4></div><div class="col-sm-2 table-col-border col-size"><h4>Valor</h4></div><div class="col-sm-2 table-col-border col-size"><h4>Operador</h4></div><div class="col-sm-2 table-col-border col-size"><h4>Nome</h4></div><div class="col-sm-1 table-col-border contain-button col-size"><button type="button" class="btn btn-block product-button removeCharacteristic"><span class="glyphicon glyphicon-remove"></span></button></div><div class="col-sm-2 col-size"></div><div class="col-sm-2 table-col-border-right table-col-border-left col-size input-padding"><input type="text" class="form-control" placeholder="'+singleCharacteristic['score']+'"></div><div class="col-sm-2 table-col-border col-size input-padding"><input type="text" class="form-control" placeholder="'+singleCharacteristic['value']+'"></div><div class="col-sm-2 table-col-border col-size input-padding"><input type="text" class="form-control" placeholder="'+singleCharacteristic['operator']+'"></div><div class="col-sm-2 table-col-border col-size input-padding"><input type="text" class="form-control charName" placeholder="'+singleCharacteristic['name']+'"></div></div></div>');

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

    $(this).parent().parent().parent().parent().remove();
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

//UTILS

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

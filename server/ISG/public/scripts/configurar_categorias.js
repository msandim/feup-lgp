//GLOBALS

//Servidor e serviços
var configs={
  server:"",
  getAllCategories:"api/allCategories",
  addCategoryApi :"api/addCategory", //?name=x&code=   -> POST
  questionsbyCategory: "api/questionsByCategory",  //-> CODE Código da categoria.  => GET Este recurso da API retorna todas as questões para uma dada categoria
  addQuestions: "api/addQuestions", //“category”: categoria onde adicionar as perguntas./“questions”: Perguntas a serem adicionadas.  => POST
  // Este recurso da API adiciona um conjunto de questões (acompanhadas das respectivas respostas, características que afetam e score respetivo).
  removeQuestion: "api/removeQuestions", // “questions”: Código das Perguntas a serem removidas.
  removeCategoryApi : "api/removeCategory", //code = Código da categoria a remover    -> DELETE
}

//arrays
var CategoryArray = [];
var CodeCategoryArray = [];
var QuestionsArray = [];
var CodeQuestionsArray = [];
//adicionar arrays para meter na db
var AddToCategory = [];
var AddQuestion = [];
var AddAnswer = [];
var AddCharacteristic = [];
//remover arrays da db
var removeQuestions = [];
var removeCategoryArr = [];

var categoryArray = [];

catItems=ko.observableArray();

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
    var spanClass = span.attr("src");
    if(spanClass == "assets\\css\\images\\seta cima.png"){
      span.attr("src","assets\\css\\images\\seta baixo.png");
    }else{
      span.attr("src","assets\\css\\images\\seta cima.png");
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
    var spanClass = span.attr("src");
    if(spanClass == "assets\\css\\images\\seta aberta.png"){
      span.attr("src","assets\\css\\images\\seta fechada.png");
    }else{
      span.attr("src","assets\\css\\images\\seta aberta.png");
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
    var spanClass = span.attr("src");
    if(spanClass == "assets\\css\\images\\seta aberta.png"){
      span.attr("src","assets\\css\\images\\seta fechada.png");
    }else{
      span.attr("src","assets\\css\\images\\seta aberta.png");
    }
  });
}

function addCategory(){
  var self = this;
  $('.AddCategory').click(function(){

    var categoryName = $('.inputCatName').val();
    var categoryCode = $('.inputCatCode').val();

    /*var CategoryExists = [];
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
    }*/

      loadService("A adicionar categoria...");
      $.ajax({
        url: configs.server+configs.addCategoryApi,
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify({name: categoryName , code: categoryCode}),
        type: "POST",
        crossDomain: true,
          }).done(function(response){
            console.log("SUCESSO   ADD category");
            loadFinished();
            location.reload();
         }).fail(function(err){
            loadFinished();
            processError(err);
            location.reload();
    })

    CategoryArray.push(categoryName);
    CodeCategoryArray.push(categoryCode);

    var boxGroup = $('#boxOfGoodies');

    categoryName = capitalizeFirstLetter(categoryName);
    boxGroup.append('<div class="box"><div class="row"><div class="col-xs-5 table-col-border-first table-col-size table-contain-h3"><h3 class="aleo-font center-cat text-color">'+categoryName+'</h3></div><div class="col-xs-3 table-col-border-first table-col-size table-contain-button"><button type="button" href="#" class="btn btn-block table-button toggle-login inputQuestion din-font-14 text-color">ADICIONAR PERGUNTA</button><div style="display:none" class="login"><div id="triangle"></div><h1>Nova Pergunta</h1><input type="text" class="value" placeholder=">" required/><input class="addQuestion" type="submit" value="ADICIONAR" /></div></div><div class="col-xs-3 table-col-border-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandCategoria din-font-14 text-color">LISTA DE PERGUNTAS/RESPOSTAS<img src="assets\\css\\images\\seta baixo.png" class="arrow-nodge"></button></div><div class="col-xs-1 table-col-border-end-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeCategory"><img src="assets\\css\\images\\x.png" class="x-size"></button></div></div><div class="box-of-questions box-of-margins"><div class="container-fluid padless-container"></div></div></div>');


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

    setTimeout(function(){
          location.reload();
    }, 1000);
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
    boxQuestions.append('<div class="box specialBox"><div class="row inside-row"><div class="col-xs-7 table-col-border table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandQuestion text-left aleo-font text-color"><img src="assets\\css\\images\\seta fechada.png" class="left-arrow-nodge">P.: '+question+'</button></div><div class="col-xs-4 table-col-border table-col-size table-contain-button"><button type="button" href="#" class="btn btn-block table-button toggle-login inputQuestion din-font-14 text-color">ADICIONAR RESPOSTA</button><div style="display:none" class="login"><div id="triangle"></div><h1 class="din-font-14 text-color">Adicionar Resposta</h1><input type="text" class="value" placeholder=">" required/><input class="addAnswer" type="submit" value="ADICIONAR" /></div></div><div class="col-xs-1 table-col-border-end table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeQuestion"><img src="assets\\css\\images\\x.png" class="x-size"></button></div></div><div class="box-of-answers"><div class="container-fluid"></div></div></div>');


    var categoryText= $(this).parent().parent().parent().find('h3').text();
    //console.log(categoryText);
    //Add question to array
    AddToCategory.push(categoryText);
    
    AddQuestion.push(categoryText);
    AddQuestion.push(question);

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
    boxAnswers.append('<div style="margin-left:4em;" class="box"><div class="row "><div class="col-xs-7 table-col-border table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandAnswer answer-background text-left aleo-font text-color"><img src="assets\\css\\images\\seta fechada.png" class="left-arrow-nodge">R.: '+answer+'</button></div><div class="col-xs-4 table-col-border table-col-size table-contain-button"><button type="button" href="#" class="btn btn-block table-button toggle-login answer-background din-font-14 text-color">ADICIONAR CARATERISTICA</button> <div style="display:none" class="login"><div id="triangle"></div><h1 class="din-font-14 text-color">Adicionar Carateristica</h1><input type="text" id="inputCharacteristic" class="value" placeholder="> Caracteristica" required/> <input type="text" id="inputOperator" class="value" placeholder="> Operador" required/><input type="text" id="inputValue" class="value" placeholder="> Valor" required/><input type="text" id="inputScore" class="value" placeholder="> Score" required/><input class="addCharacteristic" type="submit" value="ADICIONAR" /></div></div></div><div class="box-of-characteristics box-of-margins"><div class="container-fluid"></div></div></div>');


    var categoryText= $(this).closest('.box').find('h3').text();
    var questionText = $(this).parent().parent().find('.expandQuestion').text();
    questionText = questionText.substr(5,questionText.length);

    var toInsert = {
      "text": answer,
      "caracteristics":[]
    }

    //Add answer to array
    var categoryname = $(this).parent().parent().parent().find('div:first').find('button:first').text();
    //console.log(categoryname);
    AddAnswer.push(categoryname.substring(4, categoryname.length));
    AddAnswer.push(answer);

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
    boxCharacteristic.append('<div class="box"> <div class="row"><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">Caraterística</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">Operador</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">Valor</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">Score</h4></div></div><div class="row"><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">'+name+'</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4>'+operator+'</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">'+value+'</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">'+score+'</h4></div></div></div>');


    var categoryText = $(this).closest('.box-of-questions').parent().parent().find('h3').text();
    var questionText = $(this).closest('.box-of-questions').find('.expandQuestion').text();
    questionText = questionText.substr(5,questionText.length);
    var answerText = $(this).closest('.row').find('.expandAnswer').text();
    //answerText = answerText.substr(5,answerText.length);

    var toInsert = {
      "name":name,
      "operator":operator,
      "value":value,
      "score":score
    }

      //Add characteristic to array
     // console.log(answerText);
    AddCharacteristic.push(answerText.substring(4, answerText.length));
    AddCharacteristic.push(name);
    AddCharacteristic.push(operator);
    AddCharacteristic.push(value);
    AddCharacteristic.push(score);

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

               boxGroup.append('<div class="box"><div class="row"><div class="col-xs-5 table-col-border-first table-col-size table-contain-h3"><h3 class="aleo-font center-cat text-color">'+response[i][j]+'</h3></div><div class="col-xs-3 table-col-border-first table-col-size table-contain-button"><button type="button" href="#" class="btn btn-block table-button toggle-login inputQuestion din-font-14 text-color">ADICIONAR PERGUNTA</button><div style="display:none" class="login"><div id="triangle"></div><h1>Nova Pergunta</h1><input type="text" class="value" placeholder=">" required/><input class="addQuestion" type="submit" value="ADICIONAR" /></div></div><div class="col-xs-3 table-col-border-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandCategoria din-font-14 text-color">LISTA DE PERGUNTAS/RESPOSTAS<img src="assets\\css\\images\\seta baixo.png" class="arrow-nodge"></button></div><div class="col-xs-1 table-col-border-end-first table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeCategory"><img src="assets\\css\\images\\x.png" class="x-size"></button></div></div><div class="box-of-questions box-of-margins"><div class="container-fluid padless-container"></div></div></div>');

                CategoryArray.push(response[i][j]);

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

            }
            else {
               CodeCategoryArray.push(response[i][j]);
               autoAddQuestions(response[i][j],lastChild);
               console.log("CODE: " + response[i][j]);
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

function autoAddQuestions(categoryCode,lastChild){
  var self = this;

  $.ajax({
        url: configs.server+configs.questionsbyCategory+"?code="+categoryCode,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        type: "GET",
        crossDomain: true,
      }).done(function(response){
        loadFinished();

        //console.log(response);
      $.each(response, function(key,value) {

         var boxQuestions = lastChild.find('.box-of-questions').find(">:first-child");
        question=value.text;
         boxQuestions.append('<div class="box specialBox"><div class="row inside-row"><div class="col-xs-7 table-col-border table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandQuestion text-left aleo-font text-color"><img src="assets\\css\\images\\seta fechada.png" class="left-arrow-nodge">P.: '+question+'</button></div><div class="col-xs-4 table-col-border table-col-size table-contain-button"><button type="button" href="#" class="btn btn-block table-button toggle-login inputQuestion din-font-14 text-color">ADICIONAR RESPOSTA</button><div style="display:none" class="login"><div id="triangle"></div><h1 class="din-font-14 text-color">Adicionar Resposta</h1><input type="text" class="value" placeholder=">" required/><input class="addAnswer" type="submit" value="ADICIONAR" /></div></div><div class="col-xs-1 table-col-border-end table-col-size table-contain-button"><button type="button" class="btn btn-block table-button removeQuestion"><img src="assets\\css\\images\\x.png" class="x-size"></button></div></div><div class="box-of-answers"><div class="container-fluid"></div></div></div>');

        //adicionar code questions
        QuestionsArray.push(question);
        CodeQuestionsArray.push(value.code);

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
        autoAddAnswers(value.answers,newLastChild);
      });

  });
}

function autoAddAnswers(answers,lastChild){
  var self = this;

  answers.forEach(function(singleAnswer){

    var boxAnswers = lastChild.find(".box-of-answers").find(">:first-child");

    answer = capitalizeFirstLetter(singleAnswer['text']);
    boxAnswers.append('<div style="margin-left:3em;" class="box"> <div class="row"><div class="col-xs-7 table-col-border table-col-size table-contain-button"><button type="button" class="btn btn-block table-button expandAnswer answer-background text-left aleo-font text-color"><img src="assets\\css\\images\\seta fechada.png" class="left-arrow-nodge">R.: '+answer+'</button></div><div class="col-xs-4 table-col-border table-col-size table-contain-button"><button type="button" href="#" class="btn btn-block table-button toggle-login answer-background din-font-14 text-color">ADICIONAR CARATERISTICA</button> <div style="display:none" class="login"><div id="triangle"></div><h1 class="din-font-14 text-color">Adicionar Carateristica</h1><input type="text" id="inputCharacteristic" class="value" placeholder="> Caracteristica" required/> <input type="text" id="inputOperator" class="value" placeholder="> Operador" required/><input type="text" id="inputValue" class="value" placeholder="> Valor" required/><input type="text" id="inputScore" class="value" placeholder="> Score" required/><input class="addCharacteristic" type="submit" value="ADICIONAR" /></div></div></div><div class="box-of-characteristics box-of-margins"><div class="container-fluid"></div></div></div>');

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
    autoAddCharacteristics(singleAnswer['attributes'],newLastChild);

  });
}

function autoAddCharacteristics(characteristics,lastChild){
  var self = this;

  characteristics.forEach(function(singleCharacteristic){

    var boxCharacteristic = lastChild.find(".box-of-characteristics").find(">:first-child");

    boxCharacteristic.append('<div class="box"><div class="row"><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">Caraterística</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">Operador</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">Valor</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">Score</h4></div></div><div class="row"><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">'+singleCharacteristic.attribute['name']+'</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4 align="center">'+singleCharacteristic['operator']+'</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">'+singleCharacteristic['value']+'</h4></div><div class="col-xs-2 table-col-border table-col-size"><h4 align="center" class="din-font-14 text-color">'+singleCharacteristic['score']+'</h4></div></div></div>');

    var removeCharacteristicElement = boxCharacteristic.find(".removeCharacteristic");
    removeCharacteristicElement.unbind("click", removeCharacteristic());
    removeCharacteristicElement.bind("click", removeCharacteristic());

  });
}

function removeCategory() {
  var self = this;
  $('.removeCategory').click(function(){
    var codeName;
    var catElement = $(this).parent().parent().find('div:first').find("h3");
    var catName = catElement.text();
    for(var i=0; i<CategoryArray.length; i++){
      //console.log(catName + "   " +CategoryArray[i] ) ;
      if(catName==CategoryArray[i]){
        CategoryArray.splice(i,1);
        var aux = catItems.indexOf(catName);
        catItems.splice(aux,1);
        codeName = CodeCategoryArray[i];
        break;
      }
    }

    $(this).closest('.box').remove();
    //console.log(codeName + "   " + CategoryArray.length + "  " + catName);
    removeCategoryArr.push(codeName);
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

    var categoryText = $(this).parent().parent().find('div:first').find('button:first').text();
    //console.log(categoryText);
    removeQuestions.push(categoryText.substr(4,categoryText.length));

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

    $(document).on('click', '.saveChanges', function() {

      //REMOVER
      console.log(removeQuestions.length + "  " + QuestionsArray.length);
      var codesQuestions = [];
      for(var i=0; i < removeQuestions.length; i++) {
          for (var j=0; j < QuestionsArray.length; j++) {
            console.log(removeQuestions[i] + "  |  " + QuestionsArray[j]);
              if(removeQuestions[i].trim() === QuestionsArray[j].trim()){
                codesQuestions.push(CodeQuestionsArray[j]);
                //console.log("EPAA: " +AddQuestion[i]);
                break;

              }
            }
      }

      if (codesQuestions.length != 0){
        $.ajax({
                url: configs.server+configs.removeQuestion,
                type: 'DELETE',
                crossDomain: true,
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify({ "questions": codesQuestions }),
              }).done(function(response){
                   removeCategoryArr.length = 0;
                    removeQuestions.length = 0 ;
                    console.log("Sucess Remove");
              /* }).fail(function(err){
                  loadFinished();
                  processError(err);*/
              });
        }

      //REMOVER CATEGORIA
        for(var c=0; c < removeCategoryArr.length; c++) {
          $.ajax({
              url: configs.server+configs.removeCategoryApi+'?code='+removeCategoryArr[c],
              type: 'DELETE',
              crossDomain: true,
            }).done(function(response){
              removeCategoryArr.length = 0;
           }).fail(function(err){
              loadFinished();
              processError(err);
          });
        }

        //ADICIONAR
       
        for(var i=0; i < AddToCategory.length; i++) {
              var codeCat, index, namecategoria;
              for (var j=0; j < CategoryArray.length; j++) {
                if(AddToCategory[i] === CategoryArray[j]){
                  codeCat = CodeCategoryArray[j];
                  namecategoria=AddToCategory[i];
                  index = i;
                  //console.log("EPAA: " +AddQuestion[i]);
                  console.log("AQUIII " + codeCat);
                  break;
                }
              }

            var body = [];
          var answers = [];
          var attributes = [];

          for(var y=0; y < AddQuestion.length ; y++) {
            if(namecategoria ===  AddQuestion[y]) {
              y+=1;
            for(var z=0; z < AddAnswer.length; z++ ) {
              console.log("1 " + AddQuestion[y] + "  " + AddAnswer[z]);
              if(AddQuestion[y] === AddAnswer[z]) {
                z+=1;
                var attributes = [];
                for (var p = 0; p < AddCharacteristic.length; p++) {
                console.log("2 " + AddAnswer[z] + "  " + AddCharacteristic[p]);

                  if(AddAnswer[z] === AddCharacteristic[p]) {
                    attributes.push({"name": AddCharacteristic[p+1] , "operator": AddCharacteristic[p+2], "value" : AddCharacteristic[p+3], "score"  : AddCharacteristic[p+4] });
                    p += 4;
                  }
                  else {
                    p += 4;
                  }

                }

                answers.push({"text": AddAnswer[z] , attributes});

              }

            }

            body.push({"text": AddQuestion[y], answers});
            body.answers = answers;
          }
        }

          console.log(JSON.stringify(body));

          console.log(AddQuestion);
          console.log(AddAnswer);
          console.log(AddCharacteristic);

          loadService("A adicionar questões...");
        $.ajax({
            url:  configs.server+configs.addQuestions,
            type: 'POST',
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({category : codeCat , questions : body}),
            dataType : 'json',
            }).done(function(response){
              console.log("SUCESSO   ADD questions");
              loadFinished();
             location.reload();
            }).fail(function(err){
              loadFinished();
              processError(err);
               location.reload();
            });
         
         }

      });
    
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


function loadService(text){
  //$('.interface-square').empty();
  $('.loading').children().hide();
  $('.loading').append('<div id="loady"><div class="loader" id="loader"></div><div id="loadyText">'+text+'</div></div>');
  $("body").find("*").attr("disabled", "disabled");
}

function loadFinished(){
  $('#loady').remove();
  $("body").find("*").removeAttr("disabled");
  $('.loading').children().show();
}

function processError(err){
  var msg = err;
  msg = JSON.parse(msg['responseText']);
  alert(msg['msg']);
}

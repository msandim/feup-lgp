//GLOBALS

//Trocar por serviço
var categoryArray =[
  {
    'name': "Televisão",
    'image': "img/tv1.jpg"
  },
  {
    'name': "Televisão",
    'image': "img/tv1.jpg"
  },
  {
    'name': "Televisão",
    'image': "img/tv1.jpg"
  },
  {
    'name': "Not TV",
    'image': "img/tv2.jpg"
  },
  {
    'name': "Not TV",
    'image': "img/tv2.jpg"
  },
  {
    'name': "Not TV",
    'image': "img/tv2.jpg"
  },
  {
    'name': "Televisão",
    'image': "img/tv1.jpg"
  }
];

var toNextPage = {
  'category':"",
  'question-answer':[],
  'blacklist_questions':[],
  'products':[
    {
      "id":"2",
      "product_code":"XPTO-1234",
      "name":"Televisão XPTO",
      "score":0.9
    },
    {
      "id":"17",
      "product_code":"XPTO-1224",
      "name":"Televisão XPTO_2",
      "score":0.7
    }
  ],
  'history':[]
};

var service = {
  "question":{
    "id":"1",
    "text":"Qual o tamanho da sua sala?"
  },
  "answers":[
    {
      "id":"1",
      "text":"Pequena"
    },
    {
      "id":"2",
      "text":"Média"
    },
    {
      "id":"3",
      "text":"Grande"
    }
  ],
  "products":[]
};

function autoFillIndexPage(){
  var self=this;
  //var serviceCall //get categoryArray
  $('.main').empty();
  $('.main').append('<h1 class="h1-title">Escolha uma categoria</h1><div class="interface-square interface-size"></div>');
  categoryArray.forEach(function(category){
    var interfaceSquare = $('.interface-square');
    interfaceSquare.append('<div class="col-sm-4"><div class="thumbnail thumbnail-pad"><button type="button" class="btn btn-block button-pad autoFillFirstQuestionPage"><img src="'+category['image']+'" width ="200"><p class="text-center">'+category['name']+'</p></div></div>');
  });

  $('.autoFillFirstQuestionPage').click(function(){
    toNextPage['category'] = $(this).text();
    //var serviceCall pedir nova pergunta
    autoFillFirstQuestionPage();
  });

};

function autoFillFirstQuestionPage(){
  var self = this;

  var main = $('.main');
  main.empty();
  main.append('<h4 class="h4-above-title">Categoria: '+toNextPage['category']+'</h4><h1 class="h1-title">'+service['question']['text']+'</h1><div class="interface-square interface-size"></div><div class="buttons-row"><div class="row"><div class= "col-ms-2"><button type="button" class="btn btn-default backStep">Recuar</button><button type="button" class="btn btn-default pull-right skipQuestion">Avançar pergunta</button></div></div></div>');
  service['answers'].forEach(function(answer){
    var interfaceSquare = $('.interface-square');
    interfaceSquare.append('<div class="col-sm-4"><button type="button" class="btn btn-default buttons autoFillHubPage" style="display: block; width: 100%;">'+answer['text']+'</button></div>');
  });

  $('.autoFillHubPage').click(function(){
    var answerChosenText = $(this).text();
    var answerChosenID;
    service['answers'].forEach(function(answer){
      if(answer['text'] == answerChosenText){
        answerChosenID = answer['id'];
      }
    });

    toNextPage['question-answer'].push(
        {
          'question':{
            "id":service['question']['id'],
            "text":service['question']['text']
          },
          'answer':{
            "id":answerChosenID,
            "text":answerChosenText
          },
        }
    );
    toNextPage['history'].push(
        {
          'question':{
            "text":service['question']['text']
          },
          'answer':{
            "text":answerChosenText
          },
        }
    );
    //var serviceCall atualizar produtos
    autoFillHubPage();
  });

  $('.backStep').click(function(){
    autoFillIndexPage();
  });

  $('.skipQuestion').click(function(){
    var answerChosenText = $(this).text();
    toNextPage['blacklist_questions'].push(service['question']['id']);
    toNextPage['history'].push(
        {
          'question':{
            "text":service['question']['text']
          },
          'answer':{
            "text":answerChosenText
          },
        }
    );
    autoFillHubPage();
  });
};

function autoFillHubPage(){
  $('.main').empty();
  $('.main').append('<h4 class="h4-above-title" style="margin-left:0.5%;">Categoria: '+toNextPage['category']+'</h4><div class="row" style="margin-left:0.5%;"><h1 class="h1-title col-sm-8">Lista atual de produtos</h1><h1 class="h1-title col-sm-4" style="margin-left:-1%;">Histórico</h1></div><div class="row" style="margin-right:0.5%; margin-left:0.5%;"><div class="col-sm-7 interface-square interface-size product-table" style="padding-left: 20px;"></div><div class="col-sm-1"></div><div class="interface-square col-sm-4 interface-size history"></div></div><div class="buttons-row"><div class="row"><div class= "col-ms-2"><button type="button" class="btn btn-default pull-right autoFillQuestionPage">Próxima pergunta</button><button type="button" class="btn btn-default pull-right autoFillFeedbackPage" style="margin-right:10px;">Finalizar</button></div></div></div>');
  toNextPage['products'].forEach(function(product){
    $('.product-table').append('<div class="row"> <div class="col-sm-2"><div class="thumbnail"><img src="img/tv1.jpg" height ="50"></div></div><div class="col-sm-6"><p class="center-div">'+product['name']+'</p></div><div class="col-sm-2"><p class="center-div">1399,00<span class="glyphicon glyphicon-eur"></span></p></div><div class="col-sm-2 text-right"><p class="center-div-off">'+product['score']+' <span class="glyphicon glyphicon-arrow-up"></span></p><p>Soma: 49</p></div></div>');
  });
  toNextPage['history'].forEach(function(combo){
    $('.history').append('<div><p>P.: '+combo['question']['text']+'</p><p>R.: '+combo['answer']['text']+'</p></div>');
  });

  $('.autoFillQuestionPage').click(function(){
    //var serviceCall pedir nova pergunta
    autoFillQuestionPage();
  });

  $('.autoFillFeedbackPage').click(function(){
    autoFillFeedbackPage();
  });
};

function autoFillQuestionPage(){
  var self = this;

  var main = $('.main');
  main.empty();
  main.append('<h4 class="h4-above-title">Categoria: '+toNextPage['category']+'</h4><h1 class="h1-title">'+service['question']['text']+'</h1><div class="interface-square interface-size"></div><div class="buttons-row"><div class="row"><div class= "col-ms-2"><button type="button" class="btn btn-default pull-right skipQuestion">Avançar pergunta</button><button type="button" class="btn btn-default pull-right autoFillFeedbackPage" style="margin-right:10px;">Finalizar sem responder</button></div></div></div>');
  service['answers'].forEach(function(answer){
    var interfaceSquare = $('.interface-square');
    interfaceSquare.append('<div class="col-sm-4"><button type="button" class="btn btn-default buttons autoFillHubPage" style="display: block; width: 100%;">'+answer['text']+'</button></div>');
  });

  $('.autoFillHubPage').click(function(){
    var answerChosenText = $(this).text();
    var answerChosenID;
    service['answers'].forEach(function(answer){
      if(answer['text'] == answerChosenText){
        answerChosenID = answer['id'];
      }
    });

    toNextPage['question-answer'].push(
        {
          'question':{
            "id":service['question']['id'],
            "text":service['question']['text']
          },
          'answer':{
            "id":answerChosenID,
            "text":answerChosenText
          },
        }
    );
    toNextPage['history'].push(
        {
          'question':{
            "text":service['question']['text']
          },
          'answer':{
            "text":answerChosenText
          },
        }
    );
    //var serviceCall atualizar produtos
    autoFillHubPage();
  });

  $('.autoFillFeedbackPage').click(function(){
    autoFillFeedbackPage();
  });

  $('.skipQuestion').click(function(){
    var answerChosenText = $(this).text();
    toNextPage['blacklist_questions'].push(service['question']['id']);
    toNextPage['history'].push(
        {
          'question':{
            "text":service['question']['text']
          },
          'answer':{
            "text":answerChosenText
          },
        }
    );
    autoFillHubPage();
  });

};

function autoFillFeedbackPage(){
  $('.main').empty();
  $('.main').append('<h4 class="h4-above-title" style="margin-left:0.5%;">Categoria: '+toNextPage['category']+'</h4><h1 class="h1-title">Lista final de produtos</h1><div class="interface-square interface-size"></div><div class="buttons-row"><div class="row"><div class= "col-ms-2"><h4 class="pull-left">Esta sequência foi útil?</h4><button type="button" class="btn btn-default col-sm-2 pull-right no-button">Não</button><button type="button" class="btn btn-default col-sm-2 pull-right yes-button" style="margin-right:10px;">Sim</button></div></div></div>');
  toNextPage['products'].forEach(function(product){
    $('.interface-square').append('<div class="row"> <div class="col-sm-2"><div class="thumbnail"><img src="img/tv1.jpg" height ="50"></div></div><div class="col-sm-6"><p class="center-div">'+product['name']+'</p></div><div class="col-sm-2"><p class="center-div">1399,00<span class="glyphicon glyphicon-eur"></span></p></div><div class="col-sm-2 text-right"><p class="center-div">Soma: 49</p></div></div>');
  });

  $('.yes-button').click(function(){
    //var serviceCall envia valor
    finishAndRestart();
  });

  $('.no-button').click(function(){
    //var serviceCall envia valor
    finishAndRestart();
  });

};

function finishAndRestart(){
  toNextPage['category'] = "";
  toNextPage['question-answer']=[];
  toNextPage['blacklist_questions']=[];
  //toNextPage['products']=[]; descomentar quando serviço estiver a funcionar
  toNextPage['history']=[];
  autoFillIndexPage();
}

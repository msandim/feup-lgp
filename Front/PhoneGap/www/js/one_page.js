//GLOBALS

//Servidor e serviços
var configs={
	server:"http://104.167.113.111:9000",
	getNextQuestionService:"/api/getNextQuestion",
	sendFeedbackService:"/api/sendFeedback",
  getAllCategoriesService:"/api/allCategories"
}

//Trocar por serviço
var categoryArray;

var toNextPage = {
  'category':"",
	'code':"",
  'answers':[],
  'blacklist_questions':[],
  'products':[],
  'history':[]
};

var service/* = {
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
}*/;

function autoFillIndexPage(){
  var self=this;
  var serviceCall;
	var stringified;

	$('.main').empty();
	$('.main').append('<h1 class="h1-title">Escolha uma categoria</h1><div class="interface-square interface-size"></div>');
	loadService("A carregar categorias...");
  $.ajax({
        url: configs.server+configs.getAllCategoriesService,
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        type: "GET",
        crossDomain: true,
			}).done(function(response){
				loadFinished();
				categoryArray= response;
				categoryArray.forEach(function(category){
					var interfaceSquare = $('.interface-square');
					interfaceSquare.append('<div class="col-sm-4"><div class="thumbnail thumbnail-pad"><button type="button" class="btn btn-block button-pad autoFillFirstQuestionPage" id="'+category['code']+'"><img src="'/*+category['image']*/+'" width ="200"><p class="text-center">'+category['name']+'</p></div></div>');
				});

				$('.autoFillFirstQuestionPage').click(function(){
					toNextPage['category'] = $(this).text();
					toNextPage['code']=$(this).attr("id");

					serviceCall = {
													 "category": toNextPage['code'],
													 "answers": [ ],
													 "blacklist_questions": [ ]
												 };
					//pedir nova pergunta
					stringified = JSON.stringify(serviceCall);
					loadService("A obter pergunta...");
					$.ajax({
								url: configs.server+configs.getNextQuestionService,
								contentType: "application/json; charset=utf-8",
								dataType: "json",
								data: stringified,
								type: "POST",
								crossDomain: true,
					}).done(function(response){
						loadFinished();
								service = response;
								autoFillFirstQuestionPage();
					}).fail(function(err){
						loadFinished();
						processError(err);
					});
				});
			}).fail(function(err){
				loadFinished();
				processError(err);
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
        answerChosenID = answer['code'];
      }
    });

    toNextPage['answers'].push(
        {
          'question':{
            "code":service['question']['code'],
            "text":service['question']['text']
          },
          'answer':{
            "code":answerChosenID,
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
    //var serviceCall atualizar produtos e pede pergunta
		var serviceCall = {
    	"category": toNextPage['code'],
    	"answers": [],
    	"blacklist_questions": [ ]
		};
		toNextPage['answers'].forEach(function(combo){
			serviceCall['answers'].push({
				"question":combo['question']['code'],
				"answer":combo['answer']['code']
			});
		});
		toNextPage['blacklist_questions'].forEach(function(question){
			serviceCall['blacklist_questions'].push(question);
		});
		console.log(serviceCall);
		stringified = JSON.stringify(serviceCall);
		loadService("A obter produtos/preparar próxima pergunta...");
		$.ajax({
					url: configs.server+configs.getNextQuestionService,
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					data: stringified,
					type: "POST",
					crossDomain: true,
		}).done(function(response){
					loadFinished();
					service = response;
					autoFillHubPage();
		}).fail(function(err){
			loadFinished();
			processError(err);
		});
  });

  $('.backStep').click(function(){
    autoFillIndexPage();
  });

  $('.skipQuestion').click(function(){
    var answerChosenText = $(this).text();
    toNextPage['blacklist_questions'].push(service['question']['code']);
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
		//var serviceCall atualizar produtos e pede pergunta
		var serviceCall = {
    	"category": toNextPage['code'],
    	"answers": [],
    	"blacklist_questions": [ ]
		};
		toNextPage['answers'].forEach(function(combo){
			serviceCall['answers'].push({
				"question":combo['question']['code'],
				"answer":combo['answer']['code']
			});
		});
		toNextPage['blacklist_questions'].forEach(function(question){
			serviceCall['blacklist_questions'].push(question);
		});
		console.log(serviceCall);
		stringified = JSON.stringify(serviceCall);
		loadService("A obter produtos/preparar próxima pergunta...");
		$.ajax({
					url: configs.server+configs.getNextQuestionService,
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					data: stringified,
					type: "POST",
					crossDomain: true,
		}).done(function(response){
			loadFinished();
					service = response;
					console.log(service);
					autoFillHubPage();
		}).fail(function(err){
			loadFinished();
			processError(err);
		});
  });
};

function autoFillHubPage(){
  $('.main').empty();
  $('.main').append('<h4 class="h4-above-title" style="margin-left:0.5%;">Categoria: '+toNextPage['category']+'</h4><div class="row" style="margin-left:0.5%;"><h1 class="h1-title col-sm-8">Lista atual de produtos</h1><h1 class="h1-title col-sm-4" style="margin-left:-1%;">Histórico</h1></div><div class="row" style="margin-right:0.5%; margin-left:0.5%;"><div class="col-sm-7 interface-square interface-size product-table" style="padding-left: 20px;"></div><div class="col-sm-1"></div><div class="interface-square col-sm-4 interface-size history"></div></div><div class="buttons-row"><div class="row"><div class= "col-ms-2"><button type="button" class="btn btn-default pull-right autoFillQuestionPage">Próxima pergunta</button><button type="button" class="btn btn-default pull-right autoFillFeedbackPage" style="margin-right:10px;">Finalizar</button></div></div></div>');
  service['products'].forEach(function(product){
		addProductAndCalculateScoreDifference(product);
	});
	scoreSort();
	toNextPage['products'].forEach(function(product){
    $('.product-table').append('<div class="row"> <div class="col-sm-2"><div class="thumbnail"><img src="img/tv1.jpg" height ="50"></div></div><div class="col-sm-6"><p class="center-div">'+product['name']+'</p></div><div class="col-sm-2"><p class="center-div">1399,00<span class="glyphicon glyphicon-eur"></span></p></div><div class="col-sm-2 text-right"><p class="center-div-off">'+product['scoreChange']+' <span class="glyphicon glyphicon-arrow-up"></span></p><p>Score:'+product['score']+'</p></div></div>');
  });
  toNextPage['history'].forEach(function(combo){
    $('.history').append('<div><p>P.: '+combo['question']['text']+'</p><p>R.: '+combo['answer']['text']+'</p></div>');
  });

  $('.autoFillQuestionPage').click(function(){
		if(service['answers'].length==0){
			alert("Não existem mais perguntas para esta categoria. Pressione OK para redirecionar para página final");
			autoFillFeedbackPage();
		}else{
			autoFillQuestionPage();
		}
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
        answerChosenID = answer['code'];
      }
    });

    toNextPage['answers'].push(
        {
          'question':{
            "code":service['question']['code'],
            "text":service['question']['text']
          },
          'answer':{
            "code":answerChosenID,
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
		//var serviceCall atualizar produtos e pede pergunta
		var serviceCall = {
    	"category": toNextPage['code'],
    	"answers": [],
    	"blacklist_questions": [ ]
		};
		toNextPage['answers'].forEach(function(combo){
			serviceCall['answers'].push({
				"question":combo['question']['code'],
				"answer":combo['answer']['code']
			});
		});
		toNextPage['blacklist_questions'].forEach(function(question){
			serviceCall['blacklist_questions'].push(question);
		});
		console.log(serviceCall);
		stringified = JSON.stringify(serviceCall);
		loadService("A obter produtos/preparar próxima pergunta...");
		$.ajax({
					url: configs.server+configs.getNextQuestionService,
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					data: stringified,
					type: "POST",
					crossDomain: true,
		}).done(function(response){
			loadFinished();
					service = response;
					autoFillHubPage();
		}).fail(function(err){
			loadFinished();
			processError(err);
		});
    //autoFillHubPage();
  });

  $('.autoFillFeedbackPage').click(function(){
    autoFillFeedbackPage();
  });

  $('.skipQuestion').click(function(){
    var answerChosenText = $(this).text();
    toNextPage['blacklist_questions'].push(service['question']['code']);
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
		//var serviceCall atualizar produtos e pede pergunta
		var serviceCall = {
    	"category": toNextPage['code'],
    	"answers": [],
    	"blacklist_questions": [ ]
		};
		toNextPage['answers'].forEach(function(combo){
			serviceCall['answers'].push({
				"question":combo['question']['code'],
				"answer":combo['answer']['code']
			});
		});
		toNextPage['blacklist_questions'].forEach(function(question){
			serviceCall['blacklist_questions'].push(question);
		});
		console.log(serviceCall);
		stringified = JSON.stringify(serviceCall);
		loadService("A obter produtos/preparar próxima pergunta...");
		$.ajax({
					url: configs.server+configs.getNextQuestionService,
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					data: stringified,
					type: "POST",
					crossDomain: true,
		}).done(function(response){
			loadFinished();
					service = response;
					autoFillHubPage();
		}).fail(function(err){
			loadFinished();
			processError(err);
		});
  });

};

function autoFillFeedbackPage(){
  $('.main').empty();
	var serviceCall = {
		"category": toNextPage['code'],
		"answers": [],
		"feedback":-1
	};
	toNextPage['answers'].forEach(function(combo){
		serviceCall['answers'].push({
			"question":combo['question']['code'],
			"answer":combo['answer']['code']
		});
	});
  $('.main').append('<h4 class="h4-above-title" style="margin-left:0.5%;">Categoria: '+toNextPage['category']+'</h4><h1 class="h1-title">Lista final de produtos</h1><div class="interface-square interface-size"></div><div class="buttons-row"><div class="row"><div class= "col-ms-2"><h4 class="pull-left">Esta sequência foi útil?</h4><button type="button" class="btn btn-default col-sm-2 pull-right no-button">Não</button><button type="button" class="btn btn-default col-sm-2 pull-right yes-button" style="margin-right:10px;">Sim</button></div></div></div>');
  service['products'].forEach(function(product){
    $('.interface-square').append('<div class="row"> <div class="col-sm-2"><div class="thumbnail"><img src="img/tv1.jpg" height ="50"></div></div><div class="col-sm-6"><p class="center-div">'+product['name']+'</p></div><div class="col-sm-2"><p class="center-div">1399,00<span class="glyphicon glyphicon-eur"></span></p></div><div class="col-sm-2 text-right"><p class="center-div">Score:'+product['score']+'</p></div></div>');
  });

  $('.yes-button').click(function(){
    serviceCall['feedback']=1;
		stringified = JSON.stringify(serviceCall);
		loadService("A enviar feedback...");
		$.ajax({
					url: configs.server+configs.getNextQuestionService,
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					data: stringified,
					type: "POST",
					crossDomain: true,
		}).done(function(response){
			loadFinished();
					finishAndRestart();
		}).fail(function(err){
			loadFinished();
			processError(err);
		});
  });

  $('.no-button').click(function(){
    serviceCall['feedback']=0;
		stringified = JSON.stringify(serviceCall);
		loadService("A enviar feedback...");
		$.ajax({
					url: configs.server+configs.getNextQuestionService,
					contentType: "application/json; charset=utf-8",
					dataType: "json",
					data: stringified,
					type: "POST",
					crossDomain: true,
		}).done(function(response){
			loadFinished();
					finishAndRestart();
		}).fail(function(err){
			loadFinished();
			processError(err);
		});

  });

};

function finishAndRestart(){
  toNextPage['category'] = "";
	toNextPage['code']="";
  toNextPage['answers']=[];
  toNextPage['blacklist_questions']=[];
  toNextPage['products']=[];
  toNextPage['history']=[];
  autoFillIndexPage();
}

function addProductAndCalculateScoreDifference(product){
	var exists=false;
		for(var i=0; i<toNextPage['products'].length; i++){
			if(toNextPage['products'][i]['EAN'] == product['EAN']){
				toNextPage['products'][i]['scoreChange'] = Math.round( (product['score']-toNextPage['products'][i]['score']) * 10 ) / 10;
				toNextPage['products'][i]['score'] = Math.round( (product['score']) * 10 ) / 10;
				exists=true;
			}
		}

	if(!exists){
		toNextPage['products'].push(product);
		toNextPage['products'][toNextPage['products'].length-1]['scoreChange'] = Math.round( (product['score']) * 10 ) / 10;
	}
}

function scoreSort(){
	var byScore = toNextPage['products'];
	byScore.sort(function(a,b) {
		return b.score - a.score;
	});
}

function loadService(text){
	//$('.interface-square').empty();
	$('.interface-square').children().hide();
	$('.interface-square').append('<div id="loady"><div class="loader" id="loader"></div><div id="loadyText">'+text+'</div></div>');
	$("body").find("*").attr("disabled", "disabled");
}

function loadFinished(){
	$('#loady').remove();
	$("body").find("*").removeAttr("disabled");
	$('.interface-square').children().show();
}

function processError(err){
	var msg = err['responseText'];
	msg = msg.split(',');
	msg = msg[1].split(':');
	msg = msg[1].split('}');
	alert(msg[0]);
}

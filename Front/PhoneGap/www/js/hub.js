//GLOBALS

//Recebe da página anterior e atualiza para enviar para a página seguinte
var toNextPage = {
  'category':"Televisão",
  'question-answer':[
    {
      'question':{
        "id":"1",
        "text":"Qual o tamanho da sua sala?"
      },
      'answer':{
        "id":"1",
        "text":"Pequena"
      },
    }
  ],
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
  ]
};

function autoFillPage(){
  $('.main').prepend('<h4 class="h4-above-title" style="margin-left:0.5%;">Categoria: '+toNextPage['category']+'</h4>');
  toNextPage['products'].forEach(function(product){
    $('.product-table').append('<div class="row"> <div class="col-sm-2"><div class="thumbnail"><img src="img/tv1.jpg" height ="50"></div></div><div class="col-sm-6"><p class="center-div">'+product['name']+'</p></div><div class="col-sm-2"><p class="center-div">1399,00<span class="glyphicon glyphicon-eur"></span></p></div><div class="col-sm-2 text-right"><p class="center-div-off">'+product['score']+' <span class="glyphicon glyphicon-arrow-up"></span></p><p>Soma: 49</p></div></div>');
  });
  toNextPage['question-answer'].forEach(function(combo){
    $('.history').append('<div><p>P.: '+combo['question']['text']+'</p><p>R.: '+combo['answer']['text']+'</p></div>');
  });
};

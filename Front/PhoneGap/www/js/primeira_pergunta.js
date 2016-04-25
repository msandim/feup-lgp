//GLOBALS

//Recebe da página anterior e atualiza para enviar para a página seguinte
var toNextPage = {
  'category':"Televisão",
  'questions-answer':[],
  'blacklist_questions':[]
};

//Serviço get_next_question
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

function autoFillInterfaceSquare(){
  var self = this;

  var main = $('.main');
  main.prepend('<h4 class="h4-above-title">Categoria: '+toNextPage['category']+'</h4><h1 class="h1-title">'+service['question']['text']+'</h1><div class="interface-square interface-size"></div>')
  service['answers'].forEach(function(answer){
    var interfaceSquare = $('.interface-square');
    interfaceSquare.append('<div class="col-sm-4"><a href="hub.html"><button type="button" class="btn btn-default buttons" style="display: block; width: 100%;">'+answer['text']+'</button></a></div>');
  });
}

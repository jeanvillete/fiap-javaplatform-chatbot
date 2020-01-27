[intro]

O bot corrente deve ser utilizado para um cadastro simples e controle de atividades/tarefas, como por exemplo, dizer que se está concluída ou não.
Esta é uma atividade que apresenta fundamentos e estruturas de dados em JAVA aplicados no ChatBot integrado com o Telegram.

Curso: MBA FULLSTACK DEVELOPER, MICROSERVICES, CLOUD & IoT
Matéria: Java Platform
Prof. Danilo Vitoriano

---

[requisitos]
A comunicação com o chatbot deve ser através de comandos curtos, onde os comandos devem ser prefixados com o carectere barra (/) seguido pelo nome do comando.
Os possíveis comandos estão listados abaixo na sessão de comandos.

Qualquer texto enviado pelo usuário que não for no formato desejado de um comando, o comando /ajuda deverá ser escrito, informando qual o formato correto esperado.

---

[comandos]

Abaixo segue a lista de comandos e quando necessário, seus exemplos;

/start
  - inicia sessão (caso uma ainda não exista)
    - caso uma já exista, dê uma mensagem que informe isto ao usuário
  - se apresenta, indicando o que o bot está proposto a gerenciar e como deve ser feito, com a lista de comando disponível

/ajuda | /?
  - comando de ajuda, que lista todos os comando possíveis

/listar
  - listar todas as tarefas atualmente cadastradas para o usuário corrente

/nova o texto da terefa deve vir após o comando
  - o retorno deste comando deve ser um identificador, provavelmente um numérico incremental
  - listar todas as tarefas atualmente cadastradas para o usuário corrente
  e.g;
    /nova fazer tarefa de casa, aula de JAVA

/alterar:$id_tarefa o texto a ser alterado deve ser fornecido após o comando
  - procura pela tarefa com identificador fornecido, então altera o texto da mesma
  - o retorno deste comando deve ser um identificador, o mesmo fornecido no comando
  - listar todas as tarefas atualmente cadastradas para o usuário corrente
  - caso não exista nenhuma atividade/tarefa com o identificador fornecido, então informar ao usuário.
  e.g;
    /alterar:23 fazer tarefa de casa, aula de Persistência

/remover:$id_tarefa
  - procura e remove a tarefa com o identificador fornecido
  - listar todas as tarefas atualmente cadastradas para o usuário corrente
  - caso não exista nenhuma atividade/tarefa com o identificador fornecido, então informar ao usuário.
  e.g;
    /remover:23

/pronta:$id_tarefa
  - marca tarefa com o identificador fornecido como pronta
  - listar todas as tarefas atualmente cadastradas para o usuário corrente
  - caso não exista nenhuma atividade/tarefa com o identificador fornecido, então informar ao usuário.
  e.g;
    /pronta:23

/limpar
  - limpa todas as atividades da lista de tarefas do usuário corrente

/mechamede
  - comando para que o usuário possa dizer como gosta de ser chamado, ou seja, me chame de...
  - informa que vai se referenciar ao usuário pelo sem nomear, mas que se o usuário quiser indicar como gosta de ser chamado, o comando para isto é o 
  e.g;
    /mechamede Nome Usuário

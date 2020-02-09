[intro]

O bot corrente deve ser utilizado para um cadastro simples e controle de atividades/tarefas, como por exemplo, informar se determinada está concluída ou não.
Esta é uma atividade que apresenta fundamentos, concorrência e estruturas de dados em JAVA aplicados no ChatBot integrado com o Telegram.

Curso: MBA FULLSTACK DEVELOPER, MICROSERVICES, CLOUD & IoT
Matéria: Java Platform
Prof. Danilo Vitoriano

---

[instrução de execução]

Após efetuar o checkout do código, utilize o comando abaixo para excução da aplicação.
  NOTA: Requisitos, maven e Java 8.

$ mvn exec:java -Dexec.mainClass="org.telegram.chatbot.tasks.ChatApp" -Dexec.args="AQUI_VAI_O_BOT_TOKEN" -Dorg.slf4j.simpleLogger.defaultLogLevel=debug

---

[requisitos]
A comunicação com o chatbot deve ser através de comandos curtos, onde os comandos devem ser prefixados com o carectere barra (/) seguido pelo nome do comando.
Os possíveis comandos estão listados abaixo na sessão de comandos.

Qualquer texto enviado pelo usuário que não for no formato desejado de um comando, o comando /ajuda deverá ser escrito (retornado par ao usuário), informando qual o formato correto esperado.

---

[comandos]

Abaixo segue a lista de comandos, e quando necessário seus exemplos;

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

/check:$id_tarefa
  - marca tarefa com o identificador fornecido como pronta
  - caso não exista nenhuma atividade/tarefa com o identificador fornecido, então informar ao usuário.
  e.g;
    /pronta:23

/uncheck:$id_tarefa
  - marca tarefa com o identificador fornecido como NÃO pronta
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

---

[componentes]

chat.session.ChatSessionManagement
  - deve ser basicamente a base de dados da aplicação, que será mantida em memória (volátil)
  - componente responsável por gerenciar as sessões ativas
  - internamente a estrutura de dados que deve deter as informações em sessões ativas, estarão organizadas num mapa, de preferência concorrente, i.e; Map<chatId:Long, chatSession:ChatSession>
    - sugestão; ConcurrentHashMap<chatId:Long, chatSession:ChatSession>

chat.session.ChatSession
  - este componente deve conter basicamente o nome/apelido (como usuário do chat quer ser chamado) do usuário correspondente e a lista de atividades registradas
    - name:Optional<String>.of($name).getOrElse("Id " + chatId)
    - taskList:TreeSet<Task>
      - deve ser fornecido uma implementação de ordenação par os itens/tarefas (Task), que deve ser considerado a informação Task#recordTime de maneira crescente (por padrão)

chat.session.Task
  - uma instância desta classe deve conter basicamente as informações da tarefa em si, ou seja, o texto desta tarefa, e a informação se está concluída ou não.
  - além do supracitado, é necessário um campo para identificar um item neste conjunto/lista.
  - vamos manter também um campo que informe a hora que a tarefa está sendo registrada.
    - id:String
      - conteúdo automaticamente gerado no momento de registro de uma nova atividade, com conteúdo obtido do UUID.randomUUID().toString()
    - description:String
    - done:Boolean
      - no momento do registro da atividade/tarefa, esta informação inicializa com Boolean.FALSE
    - recordTime:LocalDateTime
      - conteúdo automaticamente gerado no momento de registro de uma nova atividade, com conteúdo obtido do LocalDateTime.now()

chat.command.Command
  - esta classe deve ser abstrata (ou talvez uma interface), e terá a resposabilidade de;
    - receber o texto livre como construtor, e após efetuar um String#trim() no argumento recebido, deverá avaliar se o texto livre (argumento recebido) obedece o padrão abaixo;
      - texto deve iniciar com o caractere barra (forward slash) /
      - após o caratere barra (forward slash) deve haver um texto de no mínimo dois caracteres seguidos por um espaço
      - após o espaço mencionado acima deve/pode ser fornecido texto livre que fará sentido de acordo com o os comandos definidos nas classes concretas (para um determinado comando pode fazer sentido e para outro talvez não faça sentido)
 - uma vez definido/verificado que o texto é um comando válido, deve-se então fornecer separadamente para a lista de comandos concretos a String contendo o comando e se houver, o texto após o comando
 - toda implementação concreta de um chat.command.Command deve implementar os métodos;
  - devolver uma String que diz qual é o padrão (regex) esperado para o comando (classe concreta) em questão; getRegexCommand():String
 - as classes concretas (devem estender ou implementar chat.command.Command) referente aos possíveis comandos são as seguites
    - chat.command.StartSessionCommand
    - chat.command.HelpCommand
      - NOTA: como todos as instâncias concretas de chat.command.Command deverão agir como listeners de eventos (GetUpdatesResponse) e agirão apenas em função de o texto livre ser específico para sua reação, então o comando chat.command.HelpCommand deverá entender o que toda instância concreta pode reagir, e caso o comando texto livre não se adequar e nenhuma, então o chat.command.HelpCommand deverá agir/reagir, informando que não foi compreendido o solicitado e quais são os possíveis comandos.
    - chat.command.ListTaskCommand
    - chat.command.InsertTaskCommand
    - chat.command.UpdateTaskCommand
    - chat.command.DeleteTaskCommand
    - chat.command.MarkTaskAsDoneCommand
    - chat.command.MarkTaskAsUndoneCommand
    - chat.command.EraseTaskListCommand
    - chat.command.SetUserNameCommand

ChatApp#main
  - o método "main" deve receber como argumento (o único argumento) a String que deve ser a chave do chatbot, utilizado para comunicação com o Telegram.
    - validar se foi fornecido este único argumento, caso não for, lançao exceção indicando o problema
    - envelopar o método main no contexto da instanciação do TelegramBot, para caso a chave de comunicação com a API (REST Api) do Telegram não seja válida, então lançar exceção mencionando o problema
      - NOTA: para conseguir válidar se o botToken fornecido é válido, é necessário fazer uma requisição de GetUpdatesResponse e verificar na instância retornada se a resposta está ok; GetUpdatesResponse#isOk():boolean

ChatMessageConsumer
  - este componente deve ter uma única instância e ser executado na Thread principal
  - a responsabilidade deste componente é a de iterar sobre um loop infinito de 1 em 1 segundo procurando por mensagens recebidas para o ChatBot via a API Rest do Telegram; TelegramBot#execute(new GetUpdates().limit($limit).offset($offset))
  - as mensagens/payloads (PayloadCommand, comandos texto livre) recebidas devem ser passadas integralmente para instância CommandProducer descriminada abaixo
    - NOTA: a lista de mensagens/payloads recuperados estarão disponíveis através do trecho; GetUpdatesResponse#updates():List<Update>
    - NOTA: para cada instância existente na recuperada List<Update>, deve ser instanciado um PayloadCommand, sendo fornecido as informações chatId e plainText, para que estas instâncias cheguem até as instâncias concretas de chat.command.Command, para efetuarem suas atividades

PayloadCommand
  - este componente é um POJO (transfer object) simples, que contém propriedades que necessárias para os comandos (classes concretas que implementam chat.command.Command)
  - as propriedades do POJO em questão são;
    - chatId:Long
    - plainText:String

CommandProducer
  - só deve haver uma única instância deste componente e deve ser executado na Thread principal (Thread#Main)
  - este componente tem a responsabilidade básica de manter comunicação das mensagens/payloads que estão chegando na Thread principal (Thread#Main) e disponibilizar estas mensagens (PayloadCommand) para todas as instâncias concretas de chat.command.Command que estão sendo executadas, cada uma na sua thread específica
  - na inicialização do componente CommandProducer, este deve procurar por toda classe concreta de chat.command.Command, instanciar cada uma destas classes, e obter de cada uma a instância de BlockingQueue<PayloadCommand>; getBlockingQueue():BlockingQueue<PayloadCommand>
  - o componente CommandProducer deve manter num conjunto (Set<BlockingQueue<PayloadCommand>>) todas as instâncias de BlockingQueue retornada de cada classe concreta de chat.command.Command instanciada, e toda vez que uma mensagem/payload (PayloadCommand, comando texto livre) chegar, deve-se disponibilizar este ultimo para todos itens no conjunto de BlockingQueue mantido no CommandProducer

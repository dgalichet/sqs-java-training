# Initiation à SQS

## 1. Créer une queue SQS
Créer une queue SQS dans la console avec les paramètres par défaut

## 2. Créer un Schedule event dans CloudWatch
Dans la console CloudWatch, créer une *Event Rule* avec un *scheduler* comme source (1 minute) et votre queue SQS comme *target*.
Retournez sur la console SQS et observez :
 - des messages sont disponibles dans la queue
 - dans l'onglet permissions, une nouvelle permission autorise `aws:event...` à envoyer des messages
Cette dernière règle à été ajoutée automatiquement lors de la configuration du *Scheduled Event*

## 3. Lancez l'application
- mvn clean compile exec:java
- ajoutez une policy sur l'IAM Role de votre instance permettant les opérations suivantes sur votre queue :
  - sqs:GetQueueUrl
  - sqs:ReceiveMessage
  - DeleteMessage

## 4. Configuration d'une Redrive policy
- Créez une seconde Queue `dead-test` qui servira de *dead queue*.
- Modifiez la configuration de votre première queue et ajoutez une *redrive policy* (Maximum receives = 3 par exemple)

## 5. Observer les effets du Visibility Timeout et de la Redrive Policy
- modifiez le code du consommateur pour rendre aléatoire le temps de traitement du message (entre le *Receive* et le *Delete*)
- dans la console SQS, vérifiez que des messages apparaissent *In Flight*
- si le temps de traitement dépasse le *Visibility Timeout*, les messages sont à nouveau présentés et le handle précédent n'est plus valide
- si un message est présenté un nombre de fois supérieur au *Maximum receives*, celui ci doit arriver dans la *dead queue*. Vérifiez-le.

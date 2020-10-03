/* EXAMPLE 1 .groovy , .gvy, .gy, .gsh*/

def projectIdObject = execution.getVariable('PROJECT_ID');

if(projectIdObject == null) throw new RuntimeException('Variable PROJECT_ID is required...');

def projectId = projectIdObject.toString();

def docType = 'Other Supporting Document';

def processInstId = execution.getProcessInstanceId();

def documentId = compassDocumentService.createDocumentAndDocumentLink('SGReferenceProcess', processInstId, docType).getDocumentId();

def processReference = execution.getVariable('ProcessReference').toString();

def metadata = [:];

metadata.put('Comments', processInstId);

metadata.put('PROJECT_ID', projectId);

metadata.put('ProjectNo', projectId);

metadata.put('ProcessReference', processReference);

metadata.put('ProcessType', 'SG CC Reference Flow');

metadata.put('Title', 'Other Supporting Document - ' + projectId);

compassDocumentService.addDocumentMetaData(documentId, metadata);

execution.setVariable('DOCUMENT_ID_' + docType, documentId);

 

 

 

/* EXAMPLE 2 */

def docType = 'Other Supporting Document';

execution.setVariable('TECH:DOC_END', '-- to be done --');

def documentID = execution.getVariable('DOCUMENT_ID_' + docType).toString();

 

 

/* EXAMPLE 3 */

def connection = new URL('http://krakow.com:8080/api/grant-exercise').openConnection() as HttpURLConnection;

def projectIdObject = execution.getVariable('PROJECT_ID');

def body = [projectId: projectIdObject];

connection.setDoOutput(true);

connection.setRequestProperty( 'User-Agent', 'groovy-2.5.7' );

connection.setRequestProperty('Content-Type', 'application/json');

connection.getOutputStream().write(groovy.json.JsonOutput.toJson(body).bytes);

def postRC = connection.getResponseCode();

if(postRC == 200) execution.setVariable('GRANT_EXERCISE_ID', connection.getInputStream().getText());

def getGAAmount = new URL('http://krakow.com:8080/api/grant-exercise/calculateGA').openConnection() as HttpURLConnection;

def getGAAmountRC = getGAAmount.getResponseCode();

if(getGAAmountRC == 200) execution.setVariable('GRANT_AMOUNT', getGAAmount.getInputStream().getText());

 

 

/* EXAMPLE 4 */

def clockIdentifier = 'SGReferenceProcess';

long targetDuration = 20;

long legalDuration = 40;

def numberOfInstance = '1';

def description = 'SGReferenceProcess';

def startDate = new Date();

def clockId = clockService.createClock(clockIdentifier, startDate, targetDuration, legalDuration, numberOfInstance, description);

execution.setVariable('CLOCK_ID', clockId);

execution.setVariable('CLOCK_ID_', clockId);

 

 
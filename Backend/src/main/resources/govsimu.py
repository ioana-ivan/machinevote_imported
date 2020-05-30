# govsimu est con�u pour simuler la r�ponse d'un http serveur gouvernemental
# Il renvoie une r�ponse si l'utilisateur en recherche existe
# Il identifie �galement les cas o� l'utilisateur est mineur ou non enregistr� dans la liste  

from flask import Flask
from flask import request
import requests
from flask import json
import time
import logging
app = Flask(__name__)

# Cr�er et configurer un logger
# Ceci permet de cr�er un fichier pour les logs effectu�s � travers govsimu 
# Option par d�faut pour enregistrer et traiter une erreur de processus 
logging.basicConfig(filename="newfile.log",
                    format='%(asctime)s %(message)s',
                    filemode='w',
                    level=logging.INFO)

# Cr�er un objet logger
logger=logging.getLogger()

# D�finir le threshold du logger
logger.setLevel(logging.DEBUG)

# Afficher dans le fichier log un message lors du d�marrage de govsimu
logging.info('You are logged in')

# Test de connexion
requests_log = logging.getLogger("requests.packages.urllib3")
requests_log.setLevel(logging.DEBUG)
requests_log.propagate = True
requests.get('https://httpbin.org/headers')

# Recevoir une demande d'authentification de l'�lecteur
@app.route('/electeur-authentication', methods=['POST'])

# M�thode qui prend les donn�es envoy�es par la requ�te en mode json        
def validate_voter():
    content = request.json
    
# Afficher dans le fichier log les donn�es re�ues 
    logger.info(content)

# Cas les champs donn�es sont vides    
    if(not content["CarteID"] or  not content["SecuID"]):
        userMessage={"message":"National card id or Security id is not provided"}
        return userMessage 
# Cas o� l'utilisateur est mineur       
    else:
        CarteID = content["CarteID"]
        SecuID = content["SecuID"]
        if(content["CarteID"]=="22222" or content["SecuID"]=="2222"):
            userMessage={"message":"User is Minor"}
            return userMessage
# Cas o� l'utilisateur n'est pas enregistr�         
        else:
            if(content["CarteID"]=="11111" or content["SecuID"]=="1111"):
                userMessage={"message":"User is not registered in the electoral list"}
                return userMessage  
# Cas o� l'utilisateur existe               
            else:
                userMessage={"message":"User found"}
                return userMessage
                

# Envoyer la r�ponse apr�s avoir ex�cuter la requ�te
@app.route('/list-electeur', methods=['GET'])
def listVoters():
    return json.dumps(request)

if __name__ == "__main__":
    app.run(debug=True,host='localhost', port=4000)
import requests
from tqdm import tqdm
# start simsearchdemo and create indexes by this code
with open("text.min.txt",'r',encoding='utf-8') as file:
    se = requests.session()
    for i,line in tqdm(enumerate(file.readlines())):
        id, name = line.split(",")
        se.get(f"http://localhost:8080/test1/addBatch?id={id}&name={name.strip()}")
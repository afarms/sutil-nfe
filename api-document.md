To process nfe files
```curl
curl -X POST http://localhost:8080/api/notas/processar-pdfs
```

To delete json file
```curl
curl -X DELETE http://localhost:8080/api/notas/{"5162281a-9015-405b-9808-96f5d0fc466c"}
```

To update Nota (não utilizar no gitbash pois o ã quebra)
```curl
curl -X PATCH http://localhost:8080/api/notas/update \
  -H "Content-Type: application/json; charset=UTF-8" \
  -d '{
    "id": "797edab4-0ea2-4b80-83ed-f54d0b79c473",
    "numero": "10",
    "incremento": 10.50,
    "splitPj": 10,
    "descricao": "Atualização da nota"
  }'
```
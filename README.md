# README - Postfy | Gerenciador de Email

## 📌 Descrição do Projeto

Este projeto é a parte backend da aplicação de gerenciamento de emails, permitindo que os usuários realizem operações como:

- Cadastro
- Login
- Envio de emails
- Gerenciamento de rascunhos

A aplicação é desenvolvida utilizando **Java** com o framework **Spring Boot**, proporcionando uma API RESTful robusta e segura.

---

## 🚧 Status do Projeto

**Em desenvolvimento**  
Algumas funcionalidades podem não estar completamente implementadas ou podem apresentar bugs.  
Estou trabalhando ativamente para melhorar a aplicação e adicionar novas funcionalidades.

---

## ✅ Funcionalidades

* 🧾 Cadastro de Usuário: Permite que novos usuários se cadastrem na aplicação  
* 🔐 Login: Usuários podem autenticar-se e obter um token JWT  
* 📤 Envio de Emails: Usuários podem enviar emails através da API  
* 💾 Rascunhos: Usuários podem criar e gerenciar rascunhos de emails  
* 📥 Visualização de Emails: Usuários podem listar e visualizar seus emails  

---

## 🛠 Tecnologias Utilizadas

- **Java**: Linguagem de programação utilizada para o desenvolvimento  
- **Spring Boot**: Framework para construção de aplicações Java  
- **Spring Security**: Para autenticação e autorização  
- **JWT (JSON Web Tokens)**: Para gerenciamento de sessões de usuários  
- **PostgreSQL**: Banco de dados utilizado para armazenamento de dados  

---

## 📁 Estrutura do Projeto

```
/src  
│  
├── /main  
│   ├── /java  
│   │   ├── com  
│   │   │   └── auer  
│   │   │       └── postfy  
│   │   │           ├── config  
│   │   │           ├── controller  
│   │   │           ├── dto  
│   │   │           ├── entity  
│   │   │           ├── repository  
│   │   │           ├── security  
│   │   │           └── service  
│   │   └── PostfyApplication.java  
│   └── /resources  
│       ├── application.properties  
│       └── static  
```

# Guia de Contribuição e Entregas

Este guia explica o fluxo de trabalho que a equipe deve seguir para desenvolver e entregar o projeto, utilizando branches e Pull Requests.

---

## 1. Clonando o Repositório

Para começar, apenas **um membro da equipe** precisa aceitar o convite do GitHub Classroom. Em seguida, todos os membros devem clonar o repositório para suas máquinas locais.

```bash
git clone [https://github.com/Seu-Usuario/seu-repositorio-nome.git](https://github.com/Seu-Usuario/seu-repositorio-nome.git)
cd seu-repositorio-nome
```

---

## 2. Fluxo de Trabalho por Entrega

Para cada nova etapa do projeto, siga este fluxo de trabalho. É crucial que o trabalho de cada etapa seja feito em uma branch separada.

### 2.1 Iniciar o trabalha da entrega

Antes de começar a codificar, crie uma nova branch com o nome da entrega.

```
# Sincronize com a branch principal para ter as últimas atualizações
git checkout main
git pull origin main

# Crie a nova branch para a etapa atual (ex: 'entrega-1')
git checkout -b entrega-1
```

### 2.2. Desenvolver e fazer commits

Trabalhe no projeto, adicionando e modificando arquivos. Faça commits com mensagens claras e objetivas.

```
# Adicione os arquivos que você modificou
git add . 

# Faça o commit com uma mensagem que descreve o que foi feito
git commit -m "feat: implementa a funcionalidade de login"
```

### 2.3 Fazer o push para o GitHub

Envie sua branch para o repositório remoto.

```
git push origin  entrega-2
```

### 2.4 Abri um Pull Request (PR)

Esta é a etapa mais importante para a entrega.

1. Vá para a página do seu repositório no GitHub.
1. Clique em "Compare & pull request" para a branch que você acabou de enviar.
1. Defina a branch main como a base e a sua branch (etapa-2) como a comparação.
1. Dê um título claro ao PR (ex: "Entrega Etapa 2 - Funcionalidade de Login").
1. Na descrição do PR, inclua um resumo do que foi feito e os objetivos que foram alcançados.
1. Clique em "Create pull request".

## 3. Recebendo Feedback
Após abrir o Pull Request, o professor fará a revisão do seu código, sugerindo alterações ou aprovando a entrega. Fique atento aos comentários. Quando o PR for aprovado, ele será mesclado (merge) na branch main.

## 4. Dicas Importantes

- Sempre trabalhe em uma nova branch para cada entrega.
- Mantenha suas mensagens de commit claras.
- Revise suas próprias mudanças antes de abrir o Pull Request.
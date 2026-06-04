# Modelo de Dados

## Entidades

- `Category`: categoria usada para organizar receitas.
- `Recipe`: receita, modo e tempo de preparo, notas e metadados de favorito.
- `Ingredient`: ingrediente reutilizavel entre receitas.
- `RecipeIngredient`: associacao entre receita e ingrediente, com texto original, quantidade, unidade, observacao e ordem.
- `RecipeImage`: imagem vinculada a uma receita, com caminho local e ordem.

## Relacionamentos

- Uma categoria possui varias receitas.
- Uma receita pode possuir varios ingredientes.
- Um ingrediente pode pertencer a varias receitas.
- Uma receita pode possuir varias imagens.

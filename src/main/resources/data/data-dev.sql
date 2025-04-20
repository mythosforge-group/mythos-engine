-- Insere usuários
INSERT INTO users (username, password, email, enabled)
VALUES 
  ('gandalf', 'senha123', 'gandalf@middleearth.com', true),
  ('frodo', 'precious456', 'frodo@shire.com', true);

-- Insere papéis
INSERT INTO roles (id, name)
VALUES 
  (1, 'ROLE_ADMIN'),
  (2, 'ROLE_USER');

-- Relaciona usuários com papéis
-- (assumindo que ID dos usuários sejam 1 e 2)
INSERT INTO user_roles (user_id, role_id)
VALUES 
  (1, 1),
  (2, 2);

INSERT INTO campaigns (title, description, user_id) VALUES
  ('A Sociedade do Anel', 'A jornada começa em Valfenda', 1),
  ('A Queda de Númenor', 'O destino dos homens se aproxima', 2);


INSERT INTO characters (name, background, campaign_id) VALUES
  ('Aragorn', 'Ranger do norte, herdeiro de Isildur', 1),
  ('Elrond', 'Lorde de Valfenda', 1),
  ('Sauron', 'O Senhor do Escuro', 2);
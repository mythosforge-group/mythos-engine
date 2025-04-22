-- Insere usuários
INSERT INTO users (username, password, email, enabled)
VALUES 
  ('gandalf', 'senha123',     'gandalf@middleearth.com',  true),
  ('frodo',   'precious456',  'frodo@shire.com',          true),
  ('bilbo',   'senha101112',  'bolseiro@shire.com',       true);

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
  ('A Sociedade do Anel', 'A jornada começa em Valfenda',     1),
  ('A Guerra do Anel',    'A batalha final contra Sauron',    1),
  ('As Duas Torres',      'A separação da sociedade',         2),
  ('O Retorno do Rei',    'A luta pela Terra-média',          2),
  ('A Queda de Númenor',  'O destino dos homens se aproxima', 2);


INSERT INTO characters (name, background, campaign_id) VALUES
  ('Aragorn',   'Ranger do norte, herdeiro de Isildur',         1),
  ('Elrond',    'Lorde de Valfenda',                            1),
  ('Sauron',    'O Senhor do Escuro',                           2),
  ('Gandalf',   'Mago cinzento, guia da sociedade',             1),
  ('Frodo',     'Portador do Um Anel',                          1),
  ('Sam',       'Leal amigo de Frodo',                          1),
  ('Legolas',   'Elfo de Mirkwood, arqueiro habilidoso',        1),
  ('Gimli',     'Anão de Erebor, guerreiro feroz',              1),
  ('Boromir',   'Filho de Denethor, guerreiro de Gondor',       1),
  ('Saruman',   'Mago branco, traidor da sociedade',            2),
  ('Gollum',    'Criatura corrompida pelo Um Anel',             1),
  ('Bilbo',     'Tio de Frodo, portador do Um Anel antes dele', 1),
  ('Galadriel', 'Rainha élfica de Lothlórien',                  1),
  ('Arwen',     'Filha de Elrond, amor de Aragorn',             1),
  ('Denethor',  'Senhor de Gondor, pai de Boromir e Faramir',   2),
  ('Faramir',   'Irmão de Boromir, capitão de Gondor',          2);
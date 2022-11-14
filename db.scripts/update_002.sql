CREATE TABLE if not exists post (
	id serial PRIMARY KEY,
	"name" VARCHAR(255),
	"text" text,
	"link" text UNIQUE,
	created timestamp
);
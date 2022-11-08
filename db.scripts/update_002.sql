CREATE TABLE post (
	id serial PRIMARY KEY,
	"name" VARCHAR(255),
	"text" text,
	"link" text UNIQUE,
	created date
);
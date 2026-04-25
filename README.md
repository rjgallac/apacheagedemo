


docker run \
--name myPostgresDb \
-p 5432:5432 \
-e POSTGRES_USER=postgresUser \
-e POSTGRES_PASSWORD=postgresPW \
-e POSTGRES_DB=postgresDB \
-d \
apache/age   

docker run --publish=3001:3001 --name=agviewer bitnine/agviewer:latest
P



CREATE EXTENSION age;

LOAD 'age';

SET search_path = ag_catalog, "$user", public;

SELECT create_graph('graph_name');

SELECT * 
FROM cypher('graph_name', $$
    CREATE (:label {property:"Node A"})
$$) as (v agtype);

SELECT * 
FROM cypher('graph_name', $$
    CREATE (:label {property:"Node B"})
$$) as (v agtype);

SELECT * 
FROM cypher('graph_name', $$ 
    MATCH (n) 
    RETURN n 
$$) AS (n agtype);   

SELECT * 
FROM cypher('graph_name', $$
    MATCH (a:label), (b:label)
    WHERE a.property = 'Node A' AND b.property = 'Node B'
    CREATE (a)-[e:RELTYPE {property:a.property + '<->' + b.property}]->(b)
    RETURN e
$$) as (e agtype);


SELECT * FROM cypher('graph_name', $$ MATCH result = ()-[]->() RETURN result $$) as (result agtype);

Delete all

SELECT * FROM cypher('graph_name', $$
    MATCH (n)
    DETACH DELETE n
$$) AS (n agtype);

https://github.com/apache/age/tree/master/drivers/jdbc
mvn install:install-file -Dfile=lib.jar -DgroupId=com.age -DartifactId=age -Dversion=1.0.0 -Dpackaging=jar


SELECT * 
FROM cypher('graph_name', $$
    CREATE (:label {property:"Node A"})
$$) as (v agtype);

SELECT * 
FROM cypher('graph_name', $$
    CREATE (:label {property:"Node B"})
$$) as (v agtype);

SELECT * 
FROM cypher('graph_name', $$
    MATCH (a:label), (b:label)
    WHERE a.property = 'Node A' AND b.property = 'Node B'
    CREATE (a)-[e:RELTYPE {property:a.property + '<->' + b.property}]->(b)
    RETURN e
$$) as (e agtype);

SELECT * from cypher('graph_name', $$
        MATCH (V)-[R]-(V2)
        RETURN V,R,V2
$$) as (V agtype, R agtype, V2 agtype);
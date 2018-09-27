CREATE TABLE restaurants (
    id uuid NOT NULL,
    name text NOT NULL,
    cuisines text[] NOT NULL,
    phone text NOT NULL,
    address text NOT NULL,
    description text
);

ALTER TABLE restaurants ADD PRIMARY KEY (id);
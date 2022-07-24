CREATE TABLE category (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(4000),
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT category_pk PRIMARY KEY (id)
);

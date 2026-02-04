-- Adiciona coluna canalatendimento na tabela utilizacaocontrato
-- Valor "Portal Cliente" quando a utilização foi criada pelo portal do proprietário
ALTER TABLE utilizacaocontrato
ADD COLUMN IF NOT EXISTS canalatendimento VARCHAR(50);

COMMENT ON COLUMN utilizacaocontrato.canalatendimento IS 'Canal de atendimento (ex: Portal Cliente)';

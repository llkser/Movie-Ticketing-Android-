"""empty message

Revision ID: dc14cb1e6c2c
Revises: 
Create Date: 2019-11-18 20:38:06.143910

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = 'dc14cb1e6c2c'
down_revision = None
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.add_column('goods', sa.Column('url', sa.String(length=500), nullable=True))
    op.create_index(op.f('ix_goods_url'), 'goods', ['url'], unique=False)
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_index(op.f('ix_goods_url'), table_name='goods')
    op.drop_column('goods', 'url')
    # ### end Alembic commands ###

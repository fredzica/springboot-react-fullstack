import React, { Component } from 'react';
import {
  withStyles,
  Card,
  CardContent,
  CardActions,
  Modal,
  Button,
  TextField,
} from '@material-ui/core';
import { compose } from 'recompose';
import { withRouter } from 'react-router-dom';

const styles = theme => ({
  modal: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  modalCard: {
    width: '90%',
    maxWidth: 500,
  },
  modalCardContent: {
    display: 'flex',
    flexDirection: 'column',
  },
  marginTop: {
    marginTop: 2 * theme.spacing.unit,
  },
});

class DataEditor extends Component {

  state = {data: {}, errorMsg: ''}

  componentDidMount() {
    if (this.props.data)
      // update existing
      this.setState({data: this.props.data});
    else
      // new insertion
      this.setState({data: {id: '', value: ''}});
  }

  handleSubmit = async e => {
    e.preventDefault();
    const { onSave } = this.props;
    this.setState ({errorMsg: await onSave(this.state.data)});
  }

  handleChange = e => {
    this.setState( {
      data: {id: this.state.data.id, data: e.target.value},
      errorMsg: ''
    } );
  }

  handleDecryptClick = async e => {
    const { onDecrypt } = this.props;

    this.setState ({errorMsg: '', data: {
      id: this.state.data.id,
      data: await onDecrypt(this.state.data)} 
    });
  }

  render() {
    const { classes, history } = this.props;

    return (
        <Modal
          className={classes.modal}
          onClose={() => history.goBack()}
          open
        >
          <Card className={classes.modalCard}>
            <form onSubmit={this.handleSubmit} >
              <CardContent className={classes.modalCardContent}>
                  <TextField disabled label="Id" autoFocus 
                    value={this.state.data && this.state.data.id} />
                  <TextField
                    id="value"
                    className={classes.marginTop}
                    label="Body"
                    multiline
                    rows={4}
                    error={this.state.errorMsg !== ''}
                    helperText={this.state.errorMsg}
                    value={this.state.data.data}
                    onChange={this.handleChange}
                  />
              </CardContent>
              <CardActions>
                <Button size="small" onClick={this.handleDecryptClick}>Decrypt</Button>
                <Button size="small" color="primary" type="submit">Save</Button>
                <Button size="small" onClick={() => history.goBack()}>Cancel</Button>
              </CardActions>
            </form>
          </Card>
        </Modal>
    );
  }
}

export default compose(
  withRouter,
  withStyles(styles),
)(DataEditor);

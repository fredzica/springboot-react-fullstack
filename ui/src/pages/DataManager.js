import React, { Component, Fragment } from 'react';
import { withRouter, Route, Redirect, Link } from 'react-router-dom';
import {
  withStyles,
  Typography,
  Fab,
  Paper,
  List,
  ListItem,
  ListItemText,
} from '@material-ui/core';
import { Add as AddIcon } from '@material-ui/icons';
import { find, orderBy } from 'lodash';
import { compose } from 'recompose';
import * as ellipsize from 'ellipsize';

import DataEditor from '../components/DataEditor';

const styles = theme => ({
  data: {
    marginTop: 2 * theme.spacing.unit,
  },
  fab: {
    position: 'absolute',
    bottom: 3 * theme.spacing.unit,
    right: 3 * theme.spacing.unit,
    [theme.breakpoints.down('xs')]: {
      bottom: 2 * theme.spacing.unit,
      right: 2 * theme.spacing.unit,
    },
  },
});

const API = process.env.REACT_APP_API || 'http://localhost:8080';

class DataManager extends Component {
  state = {
    loading: true,
    data: [],
  };

  componentDidMount() {
    this.getData();
  }

  async getData() {
    this.setState({ loading: false, data: await this.fetch('get', '/data') });
  }

  async fetch(method, endpoint, body) {
    try {
      const response = await fetch(`${API}${endpoint}`, {
        method,
        body: body && JSON.stringify(body),
        headers: {
          'content-type': 'application/json',
          accept: 'application/json',
        },
      });
      return await response.json();
    } catch (error) {
      console.error(error);
      return {serverError: true, resp: 'An internal server error occurred'};
    }
  }

  saveData = async (data) => {
    let result;
    if (data.id)
      result = await this.fetch('put', `/data/${data.id}`, data);
    else
      result = await this.fetch('post', '/data', data);
    
    if (result.serverError)
      return result.resp;
    else if (result.status && result.status !== 200)
      return result.errors[0].defaultMessage;


    this.props.history.goBack();
    this.getData();
    return '';
  }

  getDecryptedData = async (data) => {
    const foundData = await this.fetch('get', `/data/${data.id}/decrypted`)
    return foundData.data;
  }

  renderDataEditor = ({ match: { params: { id } } }) => {
    if (this.state.loading) return null;
    const data = find(this.state.data, { id: Number(id) });

    if (!data && id !== 'new') return <Redirect to="/data" />;

    return <DataEditor data={data} onSave={this.saveData} 
      onDecrypt={this.getDecryptedData}/>;
  };

  render() {
    const { classes } = this.props;

    return (
      <Fragment>
        <Typography variant="display1">Encrypted Data</Typography>
        {this.state.data.length > 0 ? (
          <Paper elevation={1} className={classes.data}>
            <List>
              {orderBy(this.state.data, ['id'], ['asc']).map(data => (
                <ListItem key={data.id} button component={Link} to={`/data/${data.id}`}>
                  <ListItemText
                    primary={data.id}
                    secondary={ellipsize(data.data, 60)}
                  />
                </ListItem>
              ))}
            </List>
          </Paper>
        ) : (
          !this.state.loading && <Typography variant="subheading">No encrypted data to display</Typography>
        )}
        <Fab
          color="secondary"
          aria-label="add"
          className={classes.fab}
          component={Link}
          to="/data/new"
        >
        <AddIcon />
        </Fab>
        <Route exact path="/data/:id" render={this.renderDataEditor} />

      </Fragment>
    );
  }
}

export default compose(
  withRouter,
  withStyles(styles),
)(DataManager);

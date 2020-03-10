/*
 * Copyright 2016 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

var log = require("./log.js").logger();
var fs = require('fs');
var http = require('http');
var https = require('https');
var path = require('path');
var url = require('url');
var util = require('util');
var rhea = require('rhea');
var WebSocketServer = require('ws').Server;
var AddressList = require('./address_list.js');
var BufferedSender = require('./buffered_sender.js');
var Registry = require('./registry.js');
var tls_options = require('./tls_options.js');
var myutils = require('./utils.js');
var auth_utils = require('./auth_utils.js');

function ConsoleServer (address_ctrl) {
    this.address_ctrl = address_ctrl;
    this.addresses = new AddressList();
    this.connections = new Registry();
    this.listeners = {};
    var self = this;
    this.addresses.on('updated', function (address) {
        self.publish({subject:'address',body:address});
    });
    this.addresses.on('deleted', function (address) {
        log.debug('address %s has been deleted, notifying clients...', address.address);
        self.publish({subject:'address_deleted',body:address.address});
    });
    this.connections.on('updated', function (conn) {
        self.publish({subject:'connection',body:conn});
    });
    this.connections.on('deleted', function (conn) {
        log.debug('connection %s has been deleted, notifying clients...', conn.host);
        self.publish({subject:'connection_deleted',body:conn.id});
    });
    this.amqp_container = rhea.create_container({autoaccept:false});

    this.amqp_container.on('sender_open', function (context) {
        self.subscribe(context.connection.remote.open.container_id, context.sender);
    });
    function unsubscribe (context) {
        if (context.connection.remote.open) {
            self.unsubscribe(context.connection.remote.open.container_id);
        }
    }
    var self = this;
    this.amqp_container.on('sender_close', unsubscribe);
    this.amqp_container.on('connection_close', unsubscribe);
    this.amqp_container.on('disconnected', unsubscribe);
    this.amqp_container.on('message', function (context) {
        var accept = function () {
            log.info('%s request succeeded', context.message.subject);
            context.delivery.accept();
        };
        var reject = function (e, code) {
            log.info('%s request failed: %s', context.message.subject, e);
            context.delivery.reject({condition: code || 'amqp:internal-error', description: '' + e});
        };

        if (!self.authz.is_admin(context.connection)) {
            reject(context, 'amqp:unauthorized-access', 'not authorized');
        } else if (context.message.subject === 'create_address') {
            log.info('creating address definition ' + JSON.stringify(context.message.body));
            self.address_ctrl.create_address(context.message.body).then(accept).catch(reject);
        } else if (context.message.subject === 'delete_address') {
            log.info('deleting address definition ' + context.message.body.address);
            self.address_ctrl.delete_address(context.message.body).then(accept).catch(reject);
        } else {
            reject('ignoring message: ' + context.message);
        }
    });
}

function get_cookies(request) {
    let cookies = {};
    let header = request.headers.cookie;
    if (header) {
        let items = header.split(';');
        for (let i = 0; i < items.length; i++) {
            let parts = items[i].split('=');
            cookies[parts.shift().trim()] = decodeURI(parts.join('='));
        }
    }
    return cookies;
}

ConsoleServer.prototype.ws_bind = function (server, env) {
    var self = this;
    this.ws_server = new WebSocketServer({'server': server, path: '/websocket', verifyClient:function (info, callback) {
        auth_utils.ws_auth_handler(self.authz, env)(info.req, callback);
    }});
    this.ws_server.on('connection', function (ws, request) {
        log.info('Accepted incoming websocket connection');
        self.amqp_container.websocket_accept(ws, self.authz.get_authz_props(request));
    });
};

ConsoleServer.prototype.close = function (callback) {
    var self = this;
    return new Promise(function (resolve, reject) {
        self.ws_server.close(resolve);
    }).then(function () {
        new Promise(function (resolve, reject) {
            self.server.close(resolve);
        });
    }).then(callback);
}

var content_types = {
    '.html': 'text/html',
    '.js': 'text/javascript',
    '.css': 'text/css',
    '.json': 'application/json',
    '.png': 'image/png',
    '.jpg': 'image/jpg',
    '.gif': 'image/gif',
    '.woff': 'application/font-woff',
    '.ttf': 'application/font-ttf',
    '.eot': 'application/vnd.ms-fontobject',
    '.otf': 'application/font-otf',
    '.svg': 'application/image/svg+xml'
};

function get_content_type(file) {
    return content_types[path.extname(file).toLowerCase()];
}

function static_handler(request, response, transform) {
    var file = path.join(__dirname, '../www/', url.parse(request.url).pathname);
    if (file.charAt(file.length - 1) === '/') {
        file += 'index.html';
    }
    fs.readFile(file, function (error, data) {
        if (error) {
            response.statusCode = error.code === 'ENOENT' ? 404 : 500;
            response.end(http.STATUS_CODES[response.statusCode]);
            log.warn('GET %s => %i %j', request.url, response.statusCode, error);
        } else {
            var content = transform ? transform(data) : data;
            var content_type = get_content_type(file);
            if (content_type) {
                response.setHeader('content-type', content_type);
            }
            log.debug('GET %s => %s', request.url, file);
            response.end(content);
        }
    });
}

function file_load_handler(request, response, file) {
    fs.readFile(file, function (error, data) {
        if (error) {
            response.statusCode = error.code === 'ENOENT' ? 404 : 500;
            response.end(http.STATUS_CODES[response.statusCode]);
            log.warn('GET %s => %i %j', request.url, response.statusCode, error);
        } else {
            var content_type = get_content_type(file);
            response.setHeader('content-type', 'text/plain');
            log.debug('GET %s => %s', request.url, file);
            response.end(data);
        }
    });
}

function get_create_server(env) {
    if (env.ALLOW_HTTP) {
        return http.createServer;
    } else {
        return function (callback) {
            var opts = tls_options.get_console_server_options({}, env);
            return https.createServer(opts, callback);
        }
    }
}

function replacer(original, replacement) {
    return function (data) {
        return data.toString().replace(new RegExp(original, 'g'), replacement);
    }
}

ConsoleServer.prototype.listen = function (env, callback) {
    var self = this;
    this.authz = require('./authz.js').policy(env);
    let handler = function (request, response) {
        if (request.method === 'GET') {
            try {
                if (url.parse(request.url).pathname === '/help.html' && env.MESSAGING_ROUTE_HOSTNAME !== undefined) {
                    var transform = replacer('\&lt\;messaging\-route\-hostname\&gt\;', env.MESSAGING_ROUTE_HOSTNAME);
                    static_handler(request, response,  transform);
                } else if (url.parse(request.url).pathname === '/messaging-cert.pem' && env.MESSAGING_CERT !== undefined) {
                    file_load_handler(request, response, env.MESSAGING_CERT);
                } else {
                    static_handler(request, response);
                }
            } catch (error) {
                response.statusCode = 500;
                response.end(error.message);
            }
        } else {
            response.statusCode = 405;
            response.end(util.format('%s not allowed on %s', request.method, request.url));
        }
    };
    this.server = get_create_server(env)(auth_utils.auth_handler(this.authz, env, handler));
    this.server.listen(env.port === undefined ? 8080 : env.port, callback);
    this.ws_bind(this.server, env);
    return this.server;
};

ConsoleServer.prototype.listen_probe = function (env) {
    if (env.PROBE_PORT !== undefined) {
        var probe = http.createServer(function (req, res) {
            res.writeHead(200, {'Content-Type': 'text/plain'});
            res.end('OK');
        });
        return probe.listen(env.PROBE_PORT);
    }
};

function indexer(message) {
    if (message.subject === 'address' && message.body) {
        return message.body.address;
    } else if (message.subject === 'address_deleted') {
        return message.body;
    } else if (message.subject === 'connection' && message.body) {
        return message.body.id;
    } else if (message.subject === 'connection_deleted') {
        return message.body;
    } else {
        return undefined;
    }
}

ConsoleServer.prototype.subscribe = function (name, sender) {
    var buffered_sender = new BufferedSender(sender, indexer);
    this.listeners[name] = buffered_sender;
    this.addresses.for_each(function (address) {
        buffered_sender.send({subject:'address', body:address});
    }, this.authz.address_filter(sender.connection));
    this.connections.for_each(function (conn) {
        buffered_sender.send({subject:'connection', body:conn});
    }, this.authz.connection_filter(sender.connection));
    //TODO: poll for changes in address_types
    var self = this;
    this.address_ctrl.get_address_types().then(function (address_types) {
        var props = {};
        props.address_space_type = process.env.ADDRESS_SPACE_TYPE || 'standard';
        props.disable_admin = !self.authz.is_admin(sender.connection);
        buffered_sender.send({subject:'address_types', application_properties:props, body:address_types});
    }).catch(function (error) {
        log.error('failed to get address types from address controller: %s', error);
    });
};

ConsoleServer.prototype.unsubscribe = function (name) {
    delete this.listeners[name];
};

ConsoleServer.prototype.publish = function (message) {
    for (var name in this.listeners) {
        var sender = this.listeners[name];
        if (this.authz.can_publish(sender.sender, message)) {
            sender.send(message);
        }
    }
};

module.exports = ConsoleServer;

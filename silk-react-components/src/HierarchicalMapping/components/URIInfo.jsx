import React from 'react';
import _ from 'lodash';
import { getVocabInfoAsync } from '../store';
import { NotAvailable } from '@eccenca/gui-elements';
import { isDebugMode } from '../utils/isDebugMode';

export class URIInfo extends React.Component {
    state = {
        info: false,
    };
    
    componentDidMount() {
        this.loadData(this.props);
    }
    
    componentDidUpdate(nextProps) {
        if (!_.isEqual(this.props, nextProps)) {
            this.loadData(nextProps);
        }
    }
    
    loadData(props) {
        const {uri, field} = props;
        getVocabInfoAsync(uri, field)
            .subscribe(
                ({info}) => {
                    this.setState({info});
                },
                () => {
                    isDebugMode(`Could not get any info for ${uri}@${field}`);
                    this.setState({info: false});
                }
            );
    }
    
    render() {
        const {info} = this.state;
        
        if (info) {
            return <span>{info}</span>;
        }
        
        const {
            uri, fallback, field, ...otherProps
        } = this.props;
        
        let noInfo = false;
        
        if (fallback !== undefined) {
            noInfo = fallback;
        } else if (!_.isString(uri)) {
            noInfo = <NotAvailable/>;
        } else if (field === 'label') {
            const lastHash = uri.lastIndexOf('#');
            const lastSlash = lastHash === -1 ? uri.lastIndexOf('/') : lastHash;
            noInfo = uri.substring(lastSlash + 1).replace(/[<>]/g, '');
        }
        
        return <span {...otherProps}>{noInfo}</span>;
    }
}
